# Start with a base image containing nginx
FROM nginx:1.17-alpine

# Add application nginx configuration
COPY docker/nginx.conf /etc/nginx/nginx.conf

# Remove default nginx index page
RUN rm -rf /usr/share/nginx/html/*

# Add the application files to the container
COPY dist/sgi-webapp /usr/share/nginx/html

# Make port 80 available to the world outside this container
EXPOSE 80
