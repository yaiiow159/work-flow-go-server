# Render Deployment Guide

This guide explains how to deploy the Work Flow Go Server application to Render.

## Environment Variables Configuration

When deploying to Render, you'll need to configure environment variables through the Render dashboard. Here's how to set them up:

1. Log in to your Render account and navigate to your service
2. Go to the "Environment" tab
3. Add the following environment variables:

### Required Environment Variables

```
# Database Configuration
SPRING_DATASOURCE_URL=jdbc:postgresql://your-render-postgres-host:5432/your_database_name
SPRING_DATASOURCE_USERNAME=your_username
SPRING_DATASOURCE_PASSWORD=your_password
SPRING_DATASOURCE_DRIVER_CLASS_NAME=org.postgresql.Driver

# Server Configuration
SERVER_PORT=8001
SPRING_PROFILES_ACTIVE=prod

# Cloudinary Configuration
CLOUDINARY_CLOUD_NAME=your_cloud_name
CLOUDINARY_API_KEY=your_api_key
CLOUDINARY_API_SECRET=your_api_secret

# OAuth2 Configuration
SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GOOGLE_CLIENT_ID=your_google_client_id
SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GOOGLE_CLIENT_SECRET=your_google_client_secret
SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GOOGLE_REDIRECT_URI=https://your-app-url.com/api/oauth2/callback/google

# Application Configuration
APP_OAUTH2_REDIRECTURI=https://your-app-url.com/auth/google/callback

# JPA Configuration
SPRING_JPA_HIBERNATE_DDL_AUTO=none
SPRING_JPA_SHOW_SQL=false
```

## Deployment Steps

1. Create a new Web Service on Render
2. Connect your GitHub repository
3. Configure the service with the following settings:
   - **Name**: work-flow-go-server (or your preferred name)
   - **Environment**: Docker
   - **Branch**: main (or your deployment branch)
   - **Build Command**: Leave empty (Docker will handle this)
   - **Start Command**: Leave empty (Dockerfile ENTRYPOINT will handle this)

4. Set all the environment variables listed above
5. Click "Create Web Service"

## Database Configuration

If you're using Render's PostgreSQL service:

1. Create a PostgreSQL database on Render
2. In your service's environment variables, set `SPRING_DATASOURCE_URL` to the Internal Database URL provided by Render
3. Set `SPRING_DATASOURCE_USERNAME` and `SPRING_DATASOURCE_PASSWORD` to the credentials provided by Render

## Health Checks

Render uses health checks to determine if your service is running properly. The application is configured with a health check endpoint at `/api/actuator/health`.

## Updating Your Application

When you push changes to your repository, Render will automatically rebuild and deploy your application.

## Troubleshooting

If you encounter issues with your deployment:

1. Check the Render logs for error messages
2. Verify that all environment variables are set correctly
3. Ensure your PostgreSQL database is accessible from your service
4. Check that your OAuth2 redirect URIs are configured correctly for your production domain
