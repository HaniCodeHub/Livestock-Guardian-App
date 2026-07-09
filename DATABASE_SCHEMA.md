# Database Schema

## Tables

### users
```sql
CREATE TABLE users (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  email VARCHAR(255) UNIQUE NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  name VARCHAR(255) NOT NULL,
  profile_picture_url TEXT,
  phone VARCHAR(20),
  address TEXT,
  city VARCHAR(100),
  state VARCHAR(100),
  country VARCHAR(100),
  postal_code VARCHAR(20),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  last_login TIMESTAMP,
  is_active BOOLEAN DEFAULT true
);
```

### animals
```sql
CREATE TABLE animals (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  owner_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  name VARCHAR(255) NOT NULL,
  species VARCHAR(100) NOT NULL,
  breed VARCHAR(100),
  birth_date DATE,
  gender VARCHAR(10),
  color VARCHAR(100),
  weight DECIMAL(10, 2),
  height DECIMAL(10, 2),
  microchip_id VARCHAR(50) UNIQUE,
  ear_tag VARCHAR(50) UNIQUE,
  status VARCHAR(50) DEFAULT 'active',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  is_registered BOOLEAN DEFAULT false
);
```

### muzzle_biometrics
```sql
CREATE TABLE muzzle_biometrics (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  animal_id UUID NOT NULL REFERENCES animals(id) ON DELETE CASCADE,
  image_url TEXT NOT NULL,
  feature_vector BYTEA,
  confidence_score DECIMAL(5, 4),
  capture_date TIMESTAMP,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  is_primary BOOLEAN DEFAULT false
);
```

### animal_identifications
```sql
CREATE TABLE animal_identifications (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  query_image_url TEXT NOT NULL,
  matched_animal_id UUID REFERENCES animals(id) ON DELETE SET NULL,
  confidence_score DECIMAL(5, 4),
  identified_by UUID REFERENCES users(id),
  identification_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### ownership_history
```sql
CREATE TABLE ownership_history (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  animal_id UUID NOT NULL REFERENCES animals(id) ON DELETE CASCADE,
  previous_owner_id UUID REFERENCES users(id),
  current_owner_id UUID NOT NULL REFERENCES users(id),
  transfer_date DATE,
  transfer_reason VARCHAR(255),
  document_url TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### theft_reports
```sql
CREATE TABLE theft_reports (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  animal_id UUID NOT NULL REFERENCES animals(id) ON DELETE CASCADE,
  reported_by UUID NOT NULL REFERENCES users(id),
  report_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  location VARCHAR(255),
  description TEXT,
  status VARCHAR(50) DEFAULT 'open',
  police_report_number VARCHAR(100),
  resolved_date TIMESTAMP,
  resolved_notes TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### audit_logs
```sql
CREATE TABLE audit_logs (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  user_id UUID REFERENCES users(id) ON DELETE SET NULL,
  action VARCHAR(255) NOT NULL,
  resource_type VARCHAR(100),
  resource_id UUID,
  changes JSONB,
  ip_address INET,
  user_agent TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## Indexes

```sql
CREATE INDEX idx_animals_owner_id ON animals(owner_id);
CREATE INDEX idx_animals_status ON animals(status);
CREATE INDEX idx_muzzle_biometrics_animal_id ON muzzle_biometrics(animal_id);
CREATE INDEX idx_animal_identifications_matched_animal_id ON animal_identifications(matched_animal_id);
CREATE INDEX idx_ownership_history_animal_id ON ownership_history(animal_id);
CREATE INDEX idx_theft_reports_animal_id ON theft_reports(animal_id);
CREATE INDEX idx_theft_reports_status ON theft_reports(status);
CREATE INDEX idx_audit_logs_user_id ON audit_logs(user_id);
CREATE INDEX idx_audit_logs_resource_id ON audit_logs(resource_id);
```

## Relationships

- users → animals (one-to-many)
- animals → muzzle_biometrics (one-to-many)
- animals → animal_identifications (one-to-many)
- animals → ownership_history (one-to-many)
- users → ownership_history (one-to-many)
- animals → theft_reports (one-to-many)
- users → audit_logs (one-to-many)
