# Project FAQ

## General Questions

### What is Livestock Guardian?
Livestock Guardian is an AI-powered livestock identification and theft prevention system. It uses biometric muzzle recognition to uniquely identify animals, verify ownership, and reduce theft in agriculture.

### Who can use Livestock Guardian?
The app is designed for:
- Individual farmers and livestock owners
- Agricultural cooperatives
- Livestock traders and markets
- Animal welfare organizations
- Insurance companies

### Is Livestock Guardian available in my country?
Currently available in: Kenya, Uganda, Tanzania, Rwanda
Coming soon to: Nigeria, Ghana, South Africa

Check the [Project Roadmap](ROADMAP.md) for expansion plans.

### What does Livestock Guardian cost?
**Free Tier**:
- Register up to 5 animals
- Basic identification features
- Community support

**Premium Plan** ($9.99/month):
- Unlimited animals
- Advanced analytics
- Priority support
- Custom reports

### How secure is my data?
Your data is protected by:
- AES-256 encryption at rest
- TLS 1.3 encryption in transit
- Regular security audits
- GDPR-compliant data handling
- No third-party access

See our [Security Policy](SECURITY.md) for details.

---

## Technical Questions

### System Requirements
**Android App**:
- Android 8.0 or newer
- 100MB free storage
- Camera and GPS (recommended)
- Internet connection

**Backend**:
- Python 3.9+
- PostgreSQL 12+
- 2GB RAM minimum

### How accurate is the identification?
Our muzzle recognition model achieves:
- **95%+ accuracy** on high-quality images
- **90%+ accuracy** on good-quality images
- **80%+ accuracy** on average-quality images

Accuracy improves with:
- Better lighting conditions
- Clear, unobstructed muzzle photos
- Good camera focus
- Consistent distance and angle

### Can Livestock Guardian work offline?
**Limited offline support**:
- View cached animal data
- Take photos
- Sync when connected
- Push notifications when online

Most features require internet connection.

### What image formats are supported?
- JPEG
- PNG
- WebP

Maximum file size: 10MB

### Can I integrate with my own system?
Yes! We offer:
- REST API (documented in [API_DOCUMENTATION.md](API_DOCUMENTATION.md))
- Webhook support
- Custom integrations
- Enterprise partnerships

Contact: partnerships@livestock-guardian.com

---

## Feature Questions

### How do I register an animal?
1. Open app and tap the **+** button
2. Enter animal details (name, species, breed)
3. Take a clear photo of muzzle
4. Tap **Register**

Detailed guide: [User Guide](USER_GUIDE.md)

### How does animal identification work?
1. Open camera
2. Point at animal's muzzle
3. Take photo
4. AI matches against database
5. Results show confidence score

### Can I identify animals from other users?
No, for privacy reasons. You can only identify and view animals you own, unless they're shared with you.

### How do I prove ownership?
1. Go to animal profile
2. Tap **Generate Certificate**
3. Download or share PDF
4. Certificate includes biometric verification

### How do I report a stolen animal?
1. Go to animal profile
2. Tap **Report Missing/Stolen**
3. Provide details and location
4. Alert sent to nearby users

### Can I track my animals' location?
Yes, the app records last-known location. For real-time tracking, we're developing:
- GPS collar integration
- Real-time location updates
- Geofencing alerts

---

## Data & Privacy Questions

### What data do you collect?
We collect:
- Account information (name, email, phone)
- Animal details (name, species, characteristics)
- Biometric data (muzzle photos, features)
- Location data (optional, for tracking)
- Usage analytics (anonymous)

### Can I delete my data?
Yes, you can:
1. Go to Settings → Privacy
2. Tap **Delete Account**
3. All personal data deleted within 30 days
4. Animal records retained (per law) but unlinked from you

### Who has access to my data?
- Only you (by default)
- Support team (if you request help)
- Government authorities (if required by law)

You can adjust privacy settings to share selectively.

### Is my data sold to third parties?
No. We never sell user data. We may use anonymized, aggregated data for:
- Research and development
- Improving AI models
- Market analysis

See our Privacy Policy for details.

### How long is data retained?
- Active user data: Retained while account active
- Deleted accounts: Deleted within 30 days
- Backups: Retained for 90 days
- Audit logs: Retained for 1 year

---

## Support & Troubleshooting

### How do I contact support?
- **Email**: support@livestock-guardian.com
- **In-app**: Tap Help → Contact Support
- **Community**: GitHub Discussions
- **Response time**: Within 24 hours

### My app keeps crashing
Try these steps:
1. Clear app cache: Settings → Apps → Livestock Guardian → Clear Cache
2. Update the app from Play Store
3. Restart your device
4. Reinstall if problem persists

Detailed help: [Troubleshooting Guide](TROUBLESHOOTING.md)

### Camera not working
**Check**:
1. Grant camera permission (Settings → Permissions)
2. Other apps not using camera
3. Device storage has space
4. Restart app and device

### Images not uploading
**Check**:
1. Internet connection (WiFi recommended)
2. Image size < 10MB
3. Image format (JPEG/PNG)
4. Storage space available

### I forgot my password
1. Tap **Forgot Password** on login
2. Enter your email
3. Check email for reset link
4. Click link and set new password

Not receiving email? Check:
- Spam folder
- Correct email address
- Email address associated with account

### How do I report a bug?
1. Open GitHub Issues: [Report Bug](https://github.com/HaniCodeHub/Livestock-Guardian/issues)
2. Include:
   - Steps to reproduce
   - Expected behavior
   - Actual behavior
   - Device information
   - Screenshots if possible

---

## Account & Billing

### How do I upgrade to Premium?
1. Open app
2. Go to Settings → Subscription
3. Tap **Upgrade to Premium**
4. Choose payment method
5. Confirm subscription

### What payment methods are accepted?
- Credit card (Visa, Mastercard)
- Debit card
- Mobile money (M-Pesa, AirtelMoney, etc.)
- PayPal

### Can I cancel my subscription?
Yes, anytime:
1. Go to Settings → Subscription
2. Tap **Cancel Subscription**
3. Access revoked at end of billing period
4. Data retained

### Do you offer refunds?
- Monthly subscriptions: 7-day money-back guarantee
- Annual subscriptions: No refunds after 14 days
- Contact: billing@livestock-guardian.com

---

## Development & API

### Can I use Livestock Guardian API?
Yes, for:
- Custom applications
- Integrations with farm systems
- Research and development
- Commercial use

See [API Documentation](API_DOCUMENTATION.md)

### How do I contribute?
We welcome contributions! See [Contributing Guide](CONTRIBUTING.md)

### What's the roadmap?
See detailed roadmap: [Project Roadmap](ROADMAP.md)

Key upcoming features:
- iOS app
- Web dashboard
- Advanced analytics
- Health monitoring
- Blockchain integration

---

## Still have questions?

- **Documentation**: Browse our [Knowledge Base](README.md)
- **Email**: support@livestock-guardian.com
- **Community**: GitHub Discussions
- **Social**: Follow us on Twitter, Facebook

We're here to help! 🐄
