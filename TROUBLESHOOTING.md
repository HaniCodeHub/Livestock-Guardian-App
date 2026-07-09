# Troubleshooting Guide

## Common Issues

### Android App Issues

#### 1. App Crashes on Startup
**Symptoms**: App immediately crashes after installation

**Solutions**:
- Clear app cache: Settings → Apps → Livestock Guardian → Storage → Clear Cache
- Clear app data: Settings → Apps → Livestock Guardian → Storage → Clear Data (will log you out)
- Reinstall the app: Uninstall and reinstall from Play Store
- Check device compatibility: Requires Android 8.0+
- Update Android: Go to Settings → System Updates

#### 2. Camera Not Working
**Symptoms**: Camera fails to open or capture images

**Solutions**:
- Grant camera permissions: Settings → Apps → Livestock Guardian → Permissions → Camera
- Restart the app
- Restart the device
- Check if another app is using the camera
- Update the app to latest version

#### 3. Image Upload Fails
**Symptoms**: Cannot upload muzzle images

**Solutions**:
- Check internet connection (use WiFi for faster upload)
- Verify image format (JPEG or PNG)
- Check image file size (should be < 10MB)
- Clear app cache and retry
- Log out and log back in

#### 4. Syncing Issues
**Symptoms**: Data not syncing between device and cloud

**Solutions**:
- Check internet connection
- Verify Supabase connection settings
- Log out and log back in
- Clear app cache
- Check if backend API is running

### Backend API Issues

#### 1. API Returns 401 Unauthorized
**Symptoms**: Requests failing with "Unauthorized" error

**Solutions**:
- Verify token is included in Authorization header
- Check if token has expired (tokens last 24 hours)
- Log in again to get a fresh token
- Check token format: `Authorization: Bearer <token>`

#### 2. Database Connection Failed
**Symptoms**: "Unable to connect to database" error

**Solutions**:
- Check DATABASE_URL environment variable
- Verify PostgreSQL is running
- Check database credentials
- Verify network connectivity to database host
- Check database firewall rules

#### 3. AI Model Failed to Load
**Symptoms**: Identification requests fail with "Model error"

**Solutions**:
- Verify MODEL_PATH is correct
- Check if model file exists and is readable
- Ensure sufficient disk space
- Check model file format (.pb, .h5, etc.)
- Restart the API service

#### 4. High API Response Time
**Symptoms**: API requests taking longer than expected

**Solutions**:
- Check server CPU and memory usage
- Monitor database query performance
- Check network latency
- Scale up server resources if needed
- Enable query caching

### Image Processing Issues

#### 1. Image Not Processing
**Symptoms**: Upload succeeds but no identification results

**Solutions**:
- Verify image quality (clear muzzle area)
- Check image format (JPEG/PNG)
- Ensure image is not too small or too large
- Try with a different image
- Check backend logs for errors

#### 2. Low Confidence Score
**Symptoms**: Identification succeeds but with low confidence

**Solutions**:
- Use clearer, better lit image
- Ensure muzzle area is fully visible
- Capture from similar angle to training data
- Try with multiple images
- Update AI model with newer version

### Authentication Issues

#### 1. Cannot Log In
**Symptoms**: Login fails with invalid credentials

**Solutions**:
- Verify email address is correct (case-insensitive)
- Reset password if forgotten
- Check if account is active (not suspended)
- Clear browser cache and cookies
- Try from incognito/private window

#### 2. Password Reset Not Working
**Symptoms**: Password reset email not received

**Solutions**:
- Check spam/junk folder for reset email
- Verify email address is correct
- Wait a few minutes (emails can be slow)
- Request a new reset email
- Contact support if still not working

### Performance Issues

#### 1. App Slow or Lagging
**Symptoms**: App interface feels sluggish

**Solutions**:
- Close other running apps to free memory
- Clear app cache: Settings → Apps → Livestock Guardian → Storage → Clear Cache
- Disable background processes
- Restart device
- Update app to latest version
- Check device storage (need at least 1GB free)

#### 2. Large File Handling
**Symptoms**: Slow when handling large datasets

**Solutions**:
- Use pagination for listing animals
- Implement caching
- Compress images before upload
- Use background processing for heavy operations

## Getting Help

If you're still experiencing issues:

1. **Check Documentation**: Review this guide and architecture documentation
2. **Search Issues**: Check GitHub issues for similar problems
3. **Create Issue**: Open a detailed GitHub issue with:
   - Steps to reproduce
   - Expected behavior
   - Actual behavior
   - Device/system information
   - Screenshots/logs if applicable
4. **Contact Support**: Email support@livestock-guardian.com

## Debug Logging

### Enable Debug Logging

**Android**:
```kotlin
// In MainActivity
if (BuildConfig.DEBUG) {
    Timber.plant(Timber.DebugTree())
}
```

**Backend**:
```python
import logging
logging.basicConfig(level=logging.DEBUG)
```

### Viewing Logs

**Android**:
```bash
adb logcat | grep livestock-guardian
```

**Backend**:
```bash
docker logs <container_id>
# or
tail -f application.log
```

## Common Error Messages

| Error | Cause | Solution |
|-------|-------|----------|
| `NET::ERR_CONNECTION_REFUSED` | API not running | Start API service |
| `PERMISSION_DENIED` | No camera permission | Grant permissions in settings |
| `TOKEN_EXPIRED` | JWT token expired | Log in again |
| `FILE_TOO_LARGE` | Image > 10MB | Compress image |
| `INVALID_IMAGE_FORMAT` | Not JPEG/PNG | Convert to supported format |
| `DATABASE_ERROR` | DB connection failed | Check credentials |
| `INSUFFICIENT_STORAGE` | Device storage full | Free up space |
| `NETWORK_TIMEOUT` | Network issue | Check internet connection |
