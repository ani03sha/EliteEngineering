1. P1 — Happy 2-phase flow (automatic): create PaymentIntent → confirm → PSP returns succeeded → PaymentIntent.status == succeeded → Orders moves to PAID → reservation confirmed.
2. P2 — 3DS flow: PaymentIntent requires action → client completes action → confirm again → capture succeeds.
3. P3 — Duplicate create: same Idempotency-Key twice → same PaymentIntent returned.
4. P4 — Confirm retry: network drop between confirm & PSP callback → client retries confirm with same idempotency key → no double charge.
5. P5 — Reservation expired before capture: PaymentIntent created referencing reservation but capture occurs after reservation expiry → Payment should fail with RESERVATION_EXPIRED and order cancelled/compensated.
6. P6 — Webhook replay: same webhook delivered twice → idempotent processing (check event_id or signature + dedupe).
7. Create PaymentIntent with valid reservation → 201 + clientSecret, reservationId present.
8. Confirm with same Idempotency-Key retried twice → no double charge; second confirm returns same final state.
9. Confirm after reservation expiry → 409 RESERVATION_EXPIRED or 402 with clear code.
10. Webhook replay → idempotent (second delivery ignored).