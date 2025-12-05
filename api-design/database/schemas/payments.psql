-- Payment Intents
CREATE TABLE payment_intents (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  client_secret TEXT,
  order_id UUID REFERENCES orders(id) ON DELETE SET NULL,
  reservation_id UUID,
  amount BIGINT NOT NULL,
  currency CHAR(3) NOT NULL,
  status TEXT NOT NULL,
  capture_method TEXT NOT NULL,
  created_at timestamptz DEFAULT now(),
  updated_at timestamptz DEFAULT now()
);

-- Payments (Captures/Authorization)
CREATE TABLE payments (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  payment_intent_id UUID REFERENCES payment_intents(id),
  gateway_charge_id TEXT UNIQUE, -- idempotent mapping to PSP
  amount BIGINT,
  currency CHAR(3),
  status TEXT, -- authorized, captured, refunded, failed
  created_at timestamptz DEFAULT now()
);

-- Webhooks Received
CREATE TABLE webhook_events (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  provider TEXT,
  event_type TEXT,
  payload JSONB,
  received_at timestamptz DEFAULT now(),
  processed BOOLEAN DEFAULT false
);
