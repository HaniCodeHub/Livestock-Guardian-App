# Release Management

## Version Scheme

We follow [Semantic Versioning](https://semver.org/): **MAJOR.MINOR.PATCH**

- **MAJOR**: Incompatible API changes
- **MINOR**: New functionality (backward compatible)
- **PATCH**: Bug fixes (backward compatible)

Examples:
- 1.0.0 → 1.1.0 (new feature)
- 1.1.0 → 1.1.1 (bug fix)
- 1.1.1 → 2.0.0 (breaking change)

## Release Process

### 1. Pre-Release Planning

**4 weeks before release**:
- [ ] Define release scope and features
- [ ] Create release branch: `release/v2.0.0`
- [ ] Identify breaking changes
- [ ] Plan migration path for users
- [ ] Schedule release date

**2 weeks before release**:
- [ ] Complete feature development
- [ ] Freeze new features
- [ ] Focus on bug fixes and testing
- [ ] Prepare release notes draft

**1 week before release**:
- [ ] Code freeze (except critical fixes)
- [ ] Run full test suite
- [ ] Performance testing
- [ ] Security audit
- [ ] Documentation review

### 2. Release Branch

```bash
# Create release branch
git checkout -b release/v2.0.0

# Only critical bug fixes allowed
git commit -m "Fix: Critical bug in identification"

# Prepare release
./scripts/prepare_release.sh v2.0.0

# Version bump
npm version minor

# Build artifacts
./gradlew assembleRelease
```

### 3. Testing Before Release

```bash
# Run all tests
pytest --cov=. --cov-report=term-missing
./gradlew test connectedAndroidTest

# Smoke tests
npm run test:smoke

# Performance tests
npm run test:performance

# Security scan
npm audit
bandit -r .
```

### 4. Release Artifacts

**Create**:
- [ ] Release notes
- [ ] Migration guide (if needed)
- [ ] APK/AAB for Android
- [ ] Docker image
- [ ] Python package
- [ ] Deployment checklist

**Tag release**:
```bash
git tag -a v2.0.0 -m "Release version 2.0.0"
git push origin v2.0.0
```

### 5. Deployment

**Staging First**:
```bash
# Deploy to staging
./deploy.sh staging v2.0.0

# Run smoke tests
./test/smoke_tests.sh

# Performance validation
./test/performance_tests.sh
```

**Production Rollout**:
```bash
# Blue-green deployment
./deploy.sh production v2.0.0 --strategy=blue-green

# Canary deployment (5% traffic)
./deploy.sh production v2.0.0 --strategy=canary --percentage=5

# Monitor metrics
./scripts/monitor_deployment.sh
```

### 6. Post-Release

**Immediately**:
- [ ] Verify deployment
- [ ] Monitor error rates
- [ ] Check performance metrics
- [ ] Validate user reports

**Within 24 hours**:
- [ ] Merge release branch to main
- [ ] Update documentation
- [ ] Create GitHub release
- [ ] Announce in changelog

**Within 1 week**:
- [ ] Review user feedback
- [ ] Plan patch releases if needed
- [ ] Begin next development cycle

## Branch Strategy

```
main (production)
  └── release/v2.0.0
        └── hotfix/critical-bug

develop (staging)
  └── feature/new-feature
  └── feature/another-feature
  └── bugfix/issue-123
```

### Branch Naming

- Feature: `feature/description` (e.g., `feature/muzzle-recognition`)
- Bugfix: `bugfix/issue-number` (e.g., `bugfix/issue-123`)
- Hotfix: `hotfix/description` (e.g., `hotfix/critical-memory-leak`)
- Release: `release/v1.0.0`
- Docs: `docs/update-readme`

## Release Schedule

### Regular Releases
- **Major**: Annually (Q4)
- **Minor**: Every 2 months
- **Patch**: As needed

### Release Calendar 2024

| Version | Type | Date | Status |
|---------|------|------|--------|
| 1.0.0 | Major | Jan 15 | Released |
| 1.1.0 | Minor | Mar 15 | Released |
| 1.1.1 | Patch | Mar 20 | Released |
| 2.0.0 | Major | Jul 15 | Planned |
| 2.1.0 | Minor | Sep 15 | Planned |

## Version Support Policy

| Version | Release Date | End of Life | Support Level |
|---------|------------|-------------|---------------|
| 2.0.x | Jul 15, 2024 | Jul 15, 2026 | Active |
| 1.5.x | Nov 20, 2023 | Nov 20, 2024 | Maintenance |
| 1.0.x | Sep 01, 2023 | Sep 01, 2023 | Deprecated |

**Support Levels**:
- **Active**: Bug fixes, security patches, feature improvements
- **Maintenance**: Security patches and critical bug fixes only
- **Deprecated**: No support, users encouraged to upgrade

## Release Checklist

Before releasing:

- [ ] All tests passing
- [ ] Code review completed
- [ ] Security audit done
- [ ] Performance tested
- [ ] Documentation updated
- [ ] Changelog written
- [ ] Migration guide prepared (if needed)
- [ ] Artifacts built and tested
- [ ] Staging deployment verified
- [ ] Release notes reviewed
- [ ] Communication plan ready
- [ ] Rollback plan documented

## Rollback Procedure

If critical issues found after release:

```bash
# Identify issue
./scripts/check_deployment_health.sh

# Rollback to previous version
kubectl rollout undo deployment/livestock-guardian-api

# Verify rollback
./test/smoke_tests.sh

# Investigate issue
git log v1.9.0..v2.0.0
git bisect start

# Fix and re-release
git checkout develop
git revert <commit>
git tag v2.0.1
```

## Communication

### Release Announcement

**Email**:
- Release highlights
- New features
- Breaking changes
- Upgrade instructions

**Social Media**:
- Feature highlights
- Benefits to users
- Call to action (upgrade)

**In-app**:
- Release notes popup
- Update notification
- Feature spotlight

### User Migration Support

**For breaking changes**:
- Migration guide with code examples
- Video tutorial
- Support team training
- FAQ document
- Extended support period

## Emergency Hotfix

If critical bug found in production:

```bash
# Create hotfix branch
git checkout -b hotfix/critical-bug main

# Fix the issue
# Test thoroughly
git commit -m "Fix: critical bug"

# Release as patch
git tag v2.0.1
./deploy.sh production v2.0.1 --fast-track

# Merge back
git checkout develop
git merge hotfix/critical-bug
```

**Timeline**:
- Discovery: Automatic alerts
- Fix: Within 1 hour
- Deploy: Within 2 hours
- Communicate: Within 30 minutes of fix

## Monitoring Post-Release

**First 24 hours**:
- Check error rates
- Monitor CPU/Memory usage
- Track active users
- Review user feedback

**First week**:
- Analyze usage patterns
- Collect performance metrics
- Gather user feedback
- Plan patches if needed

**Monthly**:
- Release retrospective
- Document lessons learned
- Plan improvements
- Update procedures
