# Technical Exercise

**Duration:** 30 minutes total, including code review, implementation, and discussion.

You are not expected to finish every improvement. We are interested in what you notice,
what you choose to fix first, and how you reason about the remaining risk.

## Context

We run an e-commerce checkout. A customer selects several items and pays for them at once
using a third-party payment provider such as GoPay or OVO.

This is a real production service, but it currently has a small user base and low traffic.

A junior engineer used an AI assistant to produce a first draft of this service. It compiles
and the happy path appears to work, but it has not been reviewed for production use.

Your job is to take the most important parts of it toward production.

## API

The service exposes a Spring Boot REST endpoint that accepts a purchase request as JSON.
The current mobile request contains:

- A string identifying the user or session
- A list of item id, quantity, and unit price values
- The selected payment method, such as `gopay` or `ovo`

The existing request shape is part of the code you are reviewing. Decide which values the
server can trust and which values must come from our data store. User and item details can be
read directly by this service. The service calculates the total and charges the customer.

Mobile clients run on poor connections and automatically retry requests that fail or time out.

## Payment Provider

The provider has an API with this general shape:

```text
POST https://www.gopay.com/user/{user_telephone_number}/pay
Content-Type: application/json

{
  "amount": 250000,
  "merchantReference": "checkout-123"
}
```

Operational notes:

- A successful request returns `200 OK` with a provider `referenceId` and payment status.
- p99 response latency is approximately four seconds. The recommended outbound request timeout
  is 10 seconds.
- A timeout only means our service lost the response; it does not prove that the payment failed.
  The connection can drop after the payment has already been captured.
- `GET /transaction/{referenceId}` returns the current payment status.
- The provider can look up a transaction by the merchant reference if the initial response is lost.
- Other payment providers have equivalent behavior but different URLs and field names.

Do not call a real payment provider during the exercise. Use reasoning, a fake gateway, or a
test double if you need to demonstrate a path.

## Tasks

1. Review the implementation, explain the most important problems in priority order, and
   implement the changes that matter most. A class, interface, method signature, or comment
   is enough where it communicates the design. You do not need to complete a full refactor.
2. Explain how the service should behave when a payment times out after capture and the client
   retries. Describe how you would test and monitor the highest-risk behavior without calling
   the real provider.

## Ground Rules

- Use AI assistants, documentation, and search as you normally would.
- Keep your editor and terminal visible while working.
- Think out loud. We are evaluating reasoning, not typing speed.
- It is fine to leave lower-priority cleanup unfinished. Explain what you deferred and why.
- Do not spend time on authentication, tax or discount calculation, inventory reservation,
  refunds, deployment infrastructure, or supporting every provider.

## Start Here

The complete implementation is in `src/main/java`. The most relevant files are:

```text
PurchaseController
PurchaseService
PaymentStrategy
GopayPaymentStrategy
Order
```

The repositories are in-memory to avoid database setup. Treat them as database-backed ports
for the purposes of your design discussion.
