package org.crue.hercules.sgi.tp.scheduling;

import java.util.Optional;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.MapEvent;
import com.hazelcast.core.MemberAttributeEvent;
import com.hazelcast.core.MembershipEvent;
import com.hazelcast.core.MembershipListener;
import com.hazelcast.map.listener.EntryAddedListener;
import com.hazelcast.map.listener.EntryEvictedListener;
import com.hazelcast.map.listener.EntryRemovedListener;
import com.hazelcast.map.listener.EntryUpdatedListener;
import com.hazelcast.map.listener.MapEvictedListener;

import org.crue.hercules.sgi.tp.model.BeanMethodTask;
import org.crue.hercules.sgi.tp.service.BeanMethodTaskService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SchedulingInitializer implements ApplicationListener<ApplicationReadyEvent> {
  private final BeanMethodTaskService service;
  private final BeanMethodTaskScheduler scheduler;
  // Optionally autowired, in case we don't run in a cluster (development)
  private final Optional<HazelcastInstance> hazelcast;

  public SchedulingInitializer(BeanMethodTaskScheduler scheduler, BeanMethodTaskService service,
      Optional<HazelcastInstance> hazelcast) {
    this.scheduler = scheduler;
    this.service = service;
    this.hazelcast = hazelcast;
  }

  @Override
  public void onApplicationEvent(ApplicationReadyEvent event) {
    if (hazelcast.isPresent()) {
      // Listen for cluster changes
      hazelcast.get().getCluster().addMembershipListener(new HazelcastMembershipListener());
      // Listen for data changes
      hazelcast.get().getMap(BeanMethodTaskService.TASK_CACHE_KEY).addEntryListener(new HazelcastCacheEntryListener(),
          true);
    }
    if (isMaster()) {
      log.info("There is no Master task scheduler.  Setting as Master tasks scheduler");
      initScheduling();
    } else {
      log.info("There is already a Master task scheduler.  Setting as Slave tasks scheduler");
    }
  }

  private void initScheduling() {
    log.info("Scheduling Tasks");
    scheduler.setEnabled(true);
    Page<BeanMethodTask> tasks = service.findEnabled(null, Pageable.unpaged());
    int scheduledCount = 0;
    int notScheduledCount = 0;
    for (BeanMethodTask task : tasks) {
      if (scheduler.scheduleTask(task)) {
        scheduledCount++;
      } else {
        notScheduledCount++;
      }
    }
    log.info("{} enabled tasks scheduled", scheduledCount);
    log.info("{} enabled tasks not scheduled", notScheduledCount);
  }

  private boolean isMaster() {
    // True if we are not in a cluster or
    // we are the first member of the cluster
    return (!hazelcast.isPresent() || hazelcast.get().getCluster().getMembers().iterator().next().localMember());
  }

  class HazelcastMembershipListener implements MembershipListener {
    @Override
    public void memberAdded(MembershipEvent arg0) {
      updateScheduling();
    }

    @Override
    public void memberAttributeChanged(MemberAttributeEvent arg0) {
      // Do nothing
    }

    @Override
    public void memberRemoved(MembershipEvent arg0) {
      updateScheduling();
    }

    private void updateScheduling() {
      if (isMaster()) {
        if (!scheduler.isEnabled()) {
          log.info("Scheduler removed (I am now the Master tasks scheduler");
          initScheduling();
        } else {
          log.info("Scheduler removed (nothing changed as I am still the Master tasks scheduler)");
        }
      } else {
        if (scheduler.isEnabled()) {
          log.info("Scheduler removed (I am now a Slave tasks scheduler");
          cancelScheduling();
        } else {
          log.info("Scheduler removed (nothing changed as I am still a Slave tasks scheduler)");
        }
      }
    }

    private void cancelScheduling() {
      scheduler.setEnabled(false);
      scheduler.unScheduleAll();
    }

  }

  class HazelcastCacheEntryListener
      implements EntryAddedListener<Long, BeanMethodTask>, EntryRemovedListener<Long, BeanMethodTask>,
      EntryUpdatedListener<Long, BeanMethodTask>, EntryEvictedListener<Long, BeanMethodTask>, MapEvictedListener {
    @Override
    public void entryAdded(EntryEvent<Long, BeanMethodTask> event) {
      log.debug("Entry Added: {}", event);
      BeanMethodTask task = event.getValue();
      if (!Boolean.TRUE.equals(task.getDisabled())) {
        scheduler.scheduleTask(task);
      }
    }

    @Override
    public void entryRemoved(EntryEvent<Long, BeanMethodTask> event) {
      log.debug("Entry Removed: {}", event);
      BeanMethodTask task = event.getValue();
      scheduler.unScheduleTask(task);
    }

    @Override
    public void entryUpdated(EntryEvent<Long, BeanMethodTask> event) {
      log.debug("Entry Updated: {}", event);
      BeanMethodTask task = event.getValue();
      if (Boolean.TRUE.equals(task.getDisabled())) {
        scheduler.unScheduleTask(task);
      } else {
        scheduler.scheduleTask(task);
      }
    }

    @Override
    public void entryEvicted(EntryEvent<Long, BeanMethodTask> event) {
      log.debug("Entry Evicted:" + event);
      scheduler.unScheduleTask(event.getValue());
    }

    @Override
    public void mapEvicted(MapEvent event) {
      log.debug("Map Evicted:" + event);
      initScheduling();
    }
  }
}
