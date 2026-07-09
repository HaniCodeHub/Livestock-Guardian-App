# Infrastructure & DevOps

## Infrastructure Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                        Clients                              │
│                    (Android Mobile App)                     │
└────────────────────────┬────────────────────────────────────┘
                         │
                    ┌────▼────┐
                    │   CDN   │ (CloudFlare)
                    └────┬────┘
                         │
        ┌────────────────┼────────────────┐
        │                │                │
   ┌────▼────┐    ┌─────▼─────┐    ┌────▼────┐
   │   LB    │    │    WAF    │    │ DDoS    │
   │(Nginx)  │    │(CloudFlare)   │Protection
   └────┬────┘    └─────┬─────┘    └────┬────┘
        │                │                │
        └────────────────┼────────────────┘
                         │
        ┌────────────────┼────────────────┐
        │                │                │
   ┌────▼────────┐ ┌────▼────────┐ ┌────▼────────┐
   │   API Pod   │ │  API Pod    │ │  API Pod    │
   │(FastAPI)    │ │ (FastAPI)   │ │ (FastAPI)   │
   └────┬────────┘ └────┬────────┘ └────┬────────┘
        │                │                │
        └────────────────┼────────────────┘
                         │
        ┌────────────────┼────────────────┐
        │                │                │
   ┌────▼────┐   ┌──────▼──────┐   ┌────▼────┐
   │Redis    │   │ PostgreSQL  │   │S3 Bucket│
   │(Cache)  │   │(Database)   │   │(Storage)│
   └─────────┘   └─────────────┘   └─────────┘
```

## Container & Orchestration

### Docker Compose (Development)
```yaml
version: '3.8'
services:
  api:
    image: livestock-guardian:latest
    ports:
      - "8000:8000"
    environment:
      DATABASE_URL: postgresql://user:pass@db:5432/livestock_guardian
      REDIS_URL: redis://cache:6379
    depends_on:
      - db
      - cache

  db:
    image: postgres:15
    environment:
      POSTGRES_PASSWORD: password
      POSTGRES_DB: livestock_guardian
    volumes:
      - postgres_data:/var/lib/postgresql/data

  cache:
    image: redis:7
    ports:
      - "6379:6379"

volumes:
  postgres_data:
```

### Kubernetes (Production)

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: livestock-guardian-api
spec:
  replicas: 3
  selector:
    matchLabels:
      app: livestock-guardian-api
  template:
    metadata:
      labels:
        app: livestock-guardian-api
    spec:
      containers:
      - name: api
        image: livestock-guardian:latest
        ports:
        - containerPort: 8000
        env:
        - name: DATABASE_URL
          valueFrom:
            secretKeyRef:
              name: api-secrets
              key: database_url
        resources:
          requests:
            memory: "256Mi"
            cpu: "250m"
          limits:
            memory: "512Mi"
            cpu: "500m"
        livenessProbe:
          httpGet:
            path: /health
            port: 8000
          initialDelaySeconds: 30
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /ready
            port: 8000
          initialDelaySeconds: 5
          periodSeconds: 5
---
apiVersion: v1
kind: Service
metadata:
  name: livestock-guardian-api
spec:
  selector:
    app: livestock-guardian-api
  ports:
  - port: 80
    targetPort: 8000
  type: LoadBalancer
```

## Deployment Pipeline

### CI/CD with GitHub Actions

```yaml
name: Deploy to Production

on:
  push:
    branches:
      - main

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      
      - name: Run tests
        run: |
          pip install -r requirements.txt
          pytest --cov
      
      - name: Build Docker image
        run: docker build -t livestock-guardian:${{ github.sha }} .
      
      - name: Push to registry
        run: docker push livestock-guardian:${{ github.sha }}

  deploy:
    needs: test
    runs-on: ubuntu-latest
    steps:
      - name: Deploy to Kubernetes
        run: |
          kubectl set image deployment/livestock-guardian-api \
            api=livestock-guardian:${{ github.sha }}
          kubectl rollout status deployment/livestock-guardian-api
```

## Monitoring & Logging

### Prometheus Metrics
```python
from prometheus_client import Counter, Histogram, start_http_server
import time

request_count = Counter(
    'livestock_guardian_requests_total',
    'Total requests',
    ['method', 'endpoint']
)

request_duration = Histogram(
    'livestock_guardian_request_duration_seconds',
    'Request duration in seconds',
    ['method', 'endpoint']
)

@app.middleware("http")
async def add_metrics(request, call_next):
    start = time.time()
    response = await call_next(request)
    duration = time.time() - start
    
    request_count.labels(
        method=request.method,
        endpoint=request.url.path
    ).inc()
    
    request_duration.labels(
        method=request.method,
        endpoint=request.url.path
    ).observe(duration)
    
    return response
```

### ELK Stack Setup

```yaml
version: '3.8'
services:
  elasticsearch:
    image: docker.elastic.co/elasticsearch/elasticsearch:8.0.0
    environment:
      - xpack.security.enabled=false
      - discovery.type=single-node
    ports:
      - "9200:9200"

  kibana:
    image: docker.elastic.co/kibana/kibana:8.0.0
    ports:
      - "5601:5601"

  logstash:
    image: docker.elastic.co/logstash/logstash:8.0.0
    volumes:
      - ./logstash.conf:/usr/share/logstash/pipeline/logstash.conf
    ports:
      - "5000:5000"
```

## Database Administration

### Connection Pooling
```python
from sqlalchemy import create_engine
from sqlalchemy.pool import NullPool

engine = create_engine(
    DATABASE_URL,
    poolclass=QueuePool,
    pool_size=20,
    max_overflow=40,
    pool_pre_ping=True  # Verify connections
)
```

### Replication Setup
```sql
-- Primary server
CREATE PUBLICATION replication_pub FOR TABLE animals, users;

-- Replica server
CREATE SUBSCRIPTION replication_sub CONNECTION
  'host=primary_server dbname=livestock_guardian'
  PUBLICATION replication_pub;
```

## Security Hardening

### Firewall Rules
```bash
# Allow only API traffic
sudo ufw allow 443/tcp
sudo ufw allow 80/tcp
sudo ufw allow 22/tcp (SSH for admins only)
sudo ufw deny incoming
sudo ufw allow outgoing
```

### SSL/TLS Configuration
```nginx
server {
    listen 443 ssl http2;
    server_name livestock-guardian.com;
    
    ssl_certificate /etc/ssl/certs/livestock-guardian.crt;
    ssl_certificate_key /etc/ssl/private/livestock-guardian.key;
    ssl_protocols TLSv1.3 TLSv1.2;
    ssl_ciphers HIGH:!aNULL:!MD5;
    ssl_prefer_server_ciphers on;
}
```

## Scaling Strategy

### Horizontal Scaling
- Auto-scaling groups based on CPU/Memory
- Load balancing across multiple API instances
- Database read replicas for read-heavy operations

### Vertical Scaling
- Increase instance size as needed
- Monitor resource utilization
- Right-size based on actual usage

## Cost Optimization

- Use spot instances for non-critical workloads
- Reserved instances for baseline capacity
- Auto-scaling to handle peaks
- Data compression and archival
- CDN caching to reduce bandwidth

## Disaster Recovery

See [BACKUP_RECOVERY.md](BACKUP_RECOVERY.md) for detailed procedures.

## Infrastructure Checklist

- [ ] Load balancer configured
- [ ] Auto-scaling policies set
- [ ] Database replication enabled
- [ ] Backups configured
- [ ] Monitoring alerts active
- [ ] Logging centralized
- [ ] SSL/TLS certificates valid
- [ ] Firewall rules in place
- [ ] DDoS protection enabled
- [ ] CDN configured
- [ ] Disaster recovery plan tested
- [ ] Documentation updated
