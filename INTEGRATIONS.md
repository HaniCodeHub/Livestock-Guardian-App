# Integration Guide

## Third-Party Integrations

### Payment Integration (Stripe)

```python
import stripe

stripe.api_key = "sk_live_..."

@app.post("/payment/create-checkout-session")
async def create_checkout_session(premium_plan: str):
    session = stripe.checkout.Session.create(
        payment_method_types=["card"],
        line_items=[
            {
                "price": STRIPE_PRICE_IDS[premium_plan],
                "quantity": 1,
            }
        ],
        mode="subscription",
        success_url="https://app.livestock-guardian.com/success?session_id={CHECKOUT_SESSION_ID}",
        cancel_url="https://app.livestock-guardian.com/cancel",
    )
    return {"checkout_url": session.url}
```

### Email Integration (SendGrid)

```python
from sendgrid import SendGridAPIClient
from sendgrid.helpers.mail import Mail

sg = SendGridAPIClient(os.environ.get('SENDGRID_API_KEY'))

async def send_theft_alert_email(user_email: str, animal_name: str):
    message = Mail(
        from_email='alerts@livestock-guardian.com',
        to_emails=user_email,
        subject=f'ALERT: {animal_name} may have been stolen',
        html_content=f"""
        <strong>Theft Alert</strong><br>
        Your animal {animal_name} has been reported missing.
        Please check the app for details.
        """
    )
    sg.send(message)
```

### SMS Integration (Twilio)

```python
from twilio.rest import Client

account_sid = os.environ.get("TWILIO_ACCOUNT_SID")
auth_token = os.environ.get("TWILIO_AUTH_TOKEN")
client = Client(account_sid, auth_token)

async def send_theft_alert_sms(phone_number: str, animal_name: str):
    message = client.messages.create(
        body=f"ALERT: {animal_name} reported stolen. Check Livestock Guardian app.",
        from_="+1234567890",
        to=phone_number
    )
```

### Push Notifications (Firebase Cloud Messaging)

```python
from firebase_admin import messaging

def send_push_notification(device_token: str, title: str, body: str):
    message = messaging.Message(
        notification=messaging.Notification(
            title=title,
            body=body,
        ),
        android=messaging.AndroidConfig(
            priority="high",
        ),
        token=device_token,
    )
    response = messaging.send(message)
```

### GPS/Maps Integration

```python
# Using Google Maps API
from googlemaps import Client as GoogleMapsClient

gmaps = GoogleMapsClient(key='YOUR_API_KEY')

def get_location_address(latitude: float, longitude: float):
    reverse_geocode_result = gmaps.reverse_geocode((latitude, longitude))
    return reverse_geocode_result[0]['formatted_address']

def get_distance_between_locations(origin: tuple, destination: tuple):
    distance_matrix = gmaps.distance_matrix(origin, destination)
    return distance_matrix['rows'][0]['elements'][0]['distance']['value']
```

### Weather Integration (OpenWeatherMap)

```python
import requests

def get_weather(latitude: float, longitude: float):
    url = f"https://api.openweathermap.org/data/2.5/weather"
    params = {
        'lat': latitude,
        'lon': longitude,
        'appid': os.environ.get('OPENWEATHER_API_KEY')
    }
    response = requests.get(url, params=params)
    return response.json()
```

### Veterinary Database Integration

```python
# Example: Integration with VetDB API
async def check_vet_records(animal_id: str):
    headers = {
        'Authorization': f'Bearer {VETDB_API_KEY}',
        'Content-Type': 'application/json'
    }
    async with aiohttp.ClientSession() as session:
        async with session.get(
            f"https://api.vetdb.com/animals/{animal_id}",
            headers=headers
        ) as resp:
            return await resp.json()
```

---

## Webhook Integrations

### Incoming Webhooks

```python
@app.post("/webhooks/theft-report")
async def handle_theft_report(payload: dict):
    animal_id = payload.get('animal_id')
    reporter_id = payload.get('reporter_id')
    
    # Create theft report
    theft_report = TheftReport(
        animal_id=animal_id,
        reported_by=reporter_id
    )
    db.add(theft_report)
    db.commit()
    
    # Send notifications
    await notify_animal_owner(animal_id)
    
    return {"status": "processed"}
```

### Outgoing Webhooks

```python
# Send theft report to external services
async def send_theft_report_webhook(report_id: str):
    report = db.query(TheftReport).get(report_id)
    
    webhook_url = report.organization.webhook_url
    payload = {
        'event': 'theft_report',
        'animal_id': report.animal_id,
        'timestamp': report.created_at
    }
    
    async with aiohttp.ClientSession() as session:
        await session.post(webhook_url, json=payload)
```

---

## Market Data Integration

### Livestock Price API

```python
async def get_livestock_prices(species: str, region: str):
    url = f"https://api.livestockprices.com/v1/prices"
    params = {
        'species': species,
        'region': region
    }
    async with aiohttp.ClientSession() as session:
        async with session.get(url, params=params) as resp:
            return await resp.json()
```

---

## Data Export Integrations

### Export to CSV

```python
import csv
from io import StringIO

def export_animals_to_csv(user_id: str) -> str:
    animals = db.query(Animal).filter_by(owner_id=user_id).all()
    
    output = StringIO()
    writer = csv.DictWriter(output, fieldnames=[
        'id', 'name', 'species', 'breed', 'birth_date'
    ])
    
    writer.writeheader()
    for animal in animals:
        writer.writerow({
            'id': animal.id,
            'name': animal.name,
            'species': animal.species,
            'breed': animal.breed,
            'birth_date': animal.birth_date
        })
    
    return output.getvalue()
```

### Export to PDF

```python
from reportlab.lib.pagesizes import letter
from reportlab.pdfgen import canvas

def generate_ownership_certificate(animal_id: str) -> bytes:
    from io import BytesIO
    
    animal = db.query(Animal).get(animal_id)
    buffer = BytesIO()
    
    c = canvas.Canvas(buffer, pagesize=letter)
    c.drawString(100, 750, f"Ownership Certificate")
    c.drawString(100, 700, f"Animal: {animal.name}")
    c.drawString(100, 650, f"Owner: {animal.owner.name}")
    c.drawString(100, 600, f"Date: {datetime.now()}")
    c.save()
    
    buffer.seek(0)
    return buffer.getvalue()
```

---

## Authentication Integrations

### Google OAuth

```python
from google.auth.transport import requests
from google.oauth2 import id_token

@app.post("/auth/google")
async def google_login(token: str):
    try:
        idinfo = id_token.verify_oauth2_token(
            token,
            requests.Request(),
            CLIENT_ID
        )
        email = idinfo['email']
        
        user = db.query(User).filter_by(email=email).first()
        if not user:
            user = User(email=email, name=idinfo['name'])
            db.add(user)
            db.commit()
        
        return {"token": create_jwt_token(user.id)}
    except ValueError:
        raise HTTPException(status_code=400, detail="Invalid token")
```

### Facebook OAuth

```python
@app.post("/auth/facebook")
async def facebook_login(access_token: str):
    async with aiohttp.ClientSession() as session:
        async with session.get(
            "https://graph.facebook.com/me",
            params={"access_token": access_token}
        ) as resp:
            user_info = await resp.json()
            # ... process user login
```

---

## Integration Monitoring

```python
import logging

logger = logging.getLogger(__name__)

async def monitored_integration_call(
    service_name: str,
    api_call,
    max_retries: int = 3
):
    for attempt in range(max_retries):
        try:
            result = await api_call()
            logger.info(f"Integration call to {service_name} succeeded")
            return result
        except Exception as e:
            logger.error(f"Integration call to {service_name} failed: {e}")
            if attempt == max_retries - 1:
                raise
            await asyncio.sleep(2 ** attempt)  # Exponential backoff
```

---

## Integration Checklist

- [ ] API keys stored in environment variables
- [ ] Rate limiting implemented for external APIs
- [ ] Error handling and retry logic added
- [ ] Webhook signature verification implemented
- [ ] Integration tests written
- [ ] Monitoring and logging configured
- [ ] Documentation updated
- [ ] Security review completed
