# Authentication Service (auth-service)

## User's State Diagram

```plaintext
email_unverified --[verify_email]--> active
active --[inactivity timeout]--> dormant
active --[suspend_by_admin]--> suspended
suspended --[unsuspend]--> active
active --[deactivate_by_user]--> inactive (soft_delete)
inactive --[purge_after_retention]--> hard_deleted (irreversible)
any --[delete_by_admin_with_compliance]--> hard_deleted (rare)
```

## Rules

1. **POST /v1/auth/login:**

- On success if `state == dormant`: transition `dormant -> active`, set `lastLoginAt = now()`, we re-issue refresh
  token(s) (if
  revoked), emit audit event `user_state_changed(dormant->active)`, send optional notification email.
- Return 200 with AuthResponse as usual.

2. **POST /v1/auth/register**

- Create user with `state = email_unverified` if email verification enabled, or `active` if you auto-activate.

3. **POST /v1/users/{id}/reactivate:**

- Optional endpoint. If you allow immediate login to reactivate, this endpoint can be omitted; otherwise keep it to
  support non-login reactivation flows (e.g., admin-initiated).
- Email Template to send after reactivation

```html
Hi {{firstName}},

We noticed you recently signed in to your account and it has been reactivated.

If this was you, no action is needed — welcome back!

If you do NOT recognise this activity, please:
1) Review recent sign-ins: https://app.example.com/account/security
2) Reset your password immediately: https://app.example.com/account/reset-password
3) Contact Support: support@example.com

Details:
- Account: {{email}}
- Time: {{timestamp}}
- IP: {{ip}}
- Device: {{userAgent}}

We've temporarily revoked any long-lived refresh tokens for safety. If you have trouble signing in, follow the reset link above.
```

4. **GET /v1/users/{id}:**

- Always include `state` and `lastLoginAt` in response.

5. **Error semantics:**

- `USER_SUSPENDED` → 403 (cannot login)
- `USER_INACTIVE` → 403 (account soft-deleted)
- `USER_DORMANT` is not an error on login; return success and transition (but if you ever choose to block login for
  dormant
  accounts, you'd return `USER_DORMANT` with action instructions)

## Audit Event Schema

```json
{
  "type": "user_state_changed",
  "occurredAt": "2025-12-01T12:34:56Z",
  "userId": "uuid-1234",
  "from": "dormant",
  "to": "active",
  "initiatedBy": "self-login",
  "trigger": {
    "ip": "1.2.3.4",
    "userAgent": "Mozilla/5.0 ...",
    "authMethod": "password"
  },
  "meta": {
    "reactivationNotificationSent": true,
    "notifiedAt": "2025-12-01T12:35:10Z"
  }
}
```

## Security Decisions

1. We would revoke refresh tokens on dormancy. On re-login after dormancy, we'd reissue tokens
2. We'd keep access token TTL short (15 minutes). We won't rely on revocation of access token; we'd revoke refresh
   tokens instead
3. We would treat email sending failures as operations incidents. We will record retries and alert if >N% failed.
4. We won't log sensitive data. We will log only hashed identifiers and requestId