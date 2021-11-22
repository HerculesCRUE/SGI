package org.crue.hercules.sgi.tp.scheduling;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import org.crue.hercules.sgi.tp.run.RunnableBeanMethod;
import org.crue.hercules.sgi.tp.scheduling.config.InstantTask;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.Task;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * A scheduled task registration class to add or delete cron scheduled tasks.
 */
@Component
@Slf4j
public class TaskRegistrar implements DisposableBean {

  private final Map<String, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>(16);

  private TaskScheduler taskScheduler;

  public TaskRegistrar(TaskScheduler taskScheduler) {
    this.taskScheduler = taskScheduler;
  }

  /**
   * New scheduled task.
   * <p>
   * Cron expression consists of five fields:
   * <p>
   * &lt;second&gt; &lt;minute&gt; &lt;hour&gt; &lt;dayh&gt; &lt;month&gt;
   * &lt;weekday&gt;
   * <p>
   * Special Characters in Expression:
   * <ul>
   * <li>* (all) specifies that event should happen for every time unit. For
   * example, “*” in the &lt;minute&gt; field means “for every minute.”</li>
   * <li>? (any) is utilized in the &lt;dayh&gt; and &lt;day&gt; fields to denote
   * the arbitrary value and thus neglect the field value. For example, if we want
   * to fire a script at “5th of every month” irrespective of what day of the week
   * falls on that date, we specify a “?” in the &lt;weekday&gt; field.</li>
   * <li>– (range) determines the value range. For example, “10-11” in the
   * &lt;hour&gt; field means “10th and 11th hours.”</li>
   * <li>, (values) specifies multiple values. For example, “MON, WED, FRI“ in
   * &lt;weekday&gt; field means on the days “Monday, Wednesday and Friday.”</li>
   * <li>/ (increments) specifies the incremental values. For example, a “5/15” in
   * the &lt;minute&gt; field means at “5, 20, 35 and 50 minutes of an hour.”</li>
   * <li>L (last) has different meanings when used in various fields. For example,
   * if it's applied in the &lt;dayh&gt; field, it means last day of the month,
   * i.e. “31st of January” and so on as per the calendar month. It can be used
   * with an offset value, like “L-3”, which denotes the “third to last day of the
   * calendar month.” In &lt;weekday&gt;, it specifies the “last day of a week.”
   * It can also be used with another value in &lt;weekday&gt;, like “6L”, which
   * denotes the “last Friday.”</li>
   * <li>W (weekday) determines the weekday (Monday to Friday) nearest to a given
   * day of the month. For example, if we specify “10W” in the &lt;dayh&gt; field,
   * it means the “weekday near to 10th of that month.” So if “10th” is a
   * Saturday, the job will be triggered on “9th,” and if “10th” is a Sunday, it
   * will trigger on “11th.” If we specify “1W” in &lt;dayh&gt; and if “1st” is
   * Saturday, the job will be triggered on “3rd,” which is Monday, and it will
   * not jump back to the previous month.</li>
   * <li># specifies the “N-th” occurrence of a weekday of the month, for example,
   * “third Friday of the month” can be indicated as “6#3”.</li>
   * </ul>
   * 
   * @param id             the identifier of the task to be scheduled
   * @param task           the {@link Runnable} to schedule
   * @param cronExpression the cron expression for the scheduling
   */
  public void addTask(String id, Runnable task, String cronExpression) {
    addTask(id, new CronTask(task, cronExpression));
  }

  /**
   * New scheduled task.
   * 
   * @param id        the identifier of the task to be scheduled
   * @param task      the {@link Runnable} to schedule
   * @param startTime the desired execution time for the task (if this is in the
   *                  past, the task will be executed immediately, i.e. as soon as
   *                  possible)
   */
  public void addTask(String id, Runnable task, Instant startTime) {
    addTask(id, new InstantTask(task, startTime));
  }

  /**
   * Remove scheduled tasks
   * 
   * @param id the identifier of the task to be removed from scheduled taks
   * @return true if the tasks in unscheduled
   */
  public boolean removeTask(String id) {
    ScheduledFuture<?> scheduledTask = this.scheduledTasks.remove(id);
    if (scheduledTask != null) {
      log.info("UnScheduling Task - id: {}", id);
      scheduledTask.cancel(true);
      return true;
    }
    return false;
  }

  public void removeAll() {
    log.info("UnScheduling Tasks");
    int unScheduledCount = 0;
    int notUnScheduledCount = 0;
    for (String id : this.scheduledTasks.keySet()) {
      if (removeTask(id)) {
        unScheduledCount++;
      } else {
        notUnScheduledCount++;
      }
    }
    this.scheduledTasks.clear();
    log.info("{} tasks have been unScheduled", unScheduledCount);
    log.info("{} tasks could not be unScheduled", notUnScheduledCount);
  }

  public Set<String> getScheduledTaskIds() {
    return this.scheduledTasks.keySet();
  }

  @Override
  public void destroy() {
    removeAll();
  }

  private void addTask(String id, Task task) {
    if (task != null) {
      if (this.scheduledTasks.containsKey(id)) {
        removeTask(id);
      }
      log.info("Scheduling Task - id: {}, {}, {}", id, getRunnableLogMessage(task.getRunnable()),
          getTaskLogMessage(task));
      this.scheduledTasks.put(id, scheduleTask(task));
    }
  }

  private ScheduledFuture<?> scheduleTask(Task task) {
    if (task instanceof CronTask) {
      CronTask cronTask = (CronTask) task;
      return this.taskScheduler.schedule(cronTask.getRunnable(), cronTask.getTrigger());
    } else if (task instanceof InstantTask) {
      InstantTask instantTask = (InstantTask) task;
      return this.taskScheduler.schedule(instantTask.getRunnable(), instantTask.getInstant());
    } else {
      log.error("Task type not supported {}", task.getClass().getSimpleName());
      return null;
    }
  }

  private String getRunnableLogMessage(Runnable runnable) {
    if (runnable instanceof RunnableBeanMethod) {
      RunnableBeanMethod runnableBeanMethod = (RunnableBeanMethod) runnable;
      return String.format("Runnable: %s， bean: %s， Method: %s， Parameters: %s",
          runnableBeanMethod.getClass().getSimpleName(), runnableBeanMethod.getBeanName(),
          runnableBeanMethod.getMethodName(), Arrays.toString(runnableBeanMethod.getParams()));
    } else {
      return String.format("Runnable: %s", runnable.getClass().getName());
    }
  }

  private String getTaskLogMessage(Task task) {
    if (task instanceof CronTask) {
      return String.format("Expression: %s", ((CronTask) task).getExpression());
    } else if (task instanceof InstantTask) {
      return String.format("Instant: %s", DateTimeFormatter.ISO_INSTANT.format(((InstantTask) task).getInstant()));
    } else {
      return String.format("Task type not supported %s", task.getClass().getSimpleName());
    }
  }
}