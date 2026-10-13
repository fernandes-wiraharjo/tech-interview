# Catatan Review Checkout

## Masalah prioritas tertinggi

1. Implementasi awal percaya pada `unitPrice` dari request mobile. Ini berbahaya karena harga dari client mudah dimanipulasi. Server harus menghitung total dari data item miliknya sendiri.
2. Retry dari mobile bisa menyebabkan double charge. Service perlu `idempotencyKey`, menyimpan `merchantReference` yang stabil, dan memakai ulang order yang sama saat request diulang.
3. Timeout payment bukan berarti pembayaran gagal. Timeout hanya berarti service kehilangan response. Order sebaiknya tetap `PENDING`, lalu direkonsiliasi lewat lookup provider berdasarkan `merchantReference`.
4. Payment provider sebaiknya dibungkus interface/port yang mengembalikan status eksplisit: `CAPTURED`, `FAILED`, atau `UNKNOWN`. Untuk exercise ini jangan call GoPay asli karena test jadi tidak deterministik dan berisiko.
5. Request invalid dan payment method yang tidak didukung harus menghasilkan error 400 yang jelas, bukan exception generik.

## Timeout setelah capture dan client retry

Jika provider sudah berhasil capture payment, tetapi service timeout sebelum menerima response, order yang tersimpan tetap `PENDING` dengan `merchantReference` yang sama. Saat mobile client retry memakai `idempotencyKey` yang sama, service mencari order existing, bukan membuat order baru. Setelah itu service lookup ke provider berdasarkan `merchantReference` dan menandai order sebagai `PAID` kalau provider menyatakan transaksi sudah captured. Service tidak boleh mengirim charge kedua untuk idempotency key yang sama.

Jika provider belum bisa memberi status final, API mengembalikan status `PENDING`. Di production, background reconciliation job perlu mengecek pending order secara berkala dengan backoff sampai status menjadi final atau perlu investigasi manual.

## Test dan monitoring

Test payment flow dengan fake `PaymentStrategy`, bukan provider asli:

- harga dari client diabaikan dan harga dari repository yang dipakai
- duplicate request dengan idempotency key yang sama mengembalikan order/result yang sama
- timeout atau status provider `UNKNOWN` membuat order tetap `PENDING`
- retry untuk pending order melakukan lookup provider by merchant reference, bukan charge ulang
- status provider `FAILED` menandai order failed tanpa retry charge secara membabi buta

Monitoring production sebaiknya melacak jumlah payment attempt, timeout dari provider, umur pending order, sukses/gagal reconciliation, pemakaian duplicate idempotency key, dan mismatch antara status order lokal dengan status provider. Log perlu menyertakan order id, merchant reference, provider reference jika ada, dan payment method, tetapi jangan menyimpan data user yang sensitif.
