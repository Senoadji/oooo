import http.server
import socketserver
import os

PORT = 3000
APK_PATH = os.path.abspath("app/build/outputs/apk/debug/app-debug.apk")
FALLBACK_APK_PATH = os.path.abspath(".build-outputs/app-debug.apk")

HTML_CONTENT = """<!DOCTYPE html>
<html lang="id">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sistem Kas & Ronda RT 06 - Download & Informasi</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;600;700;800&display=swap" rel="stylesheet">
    <style>
        :root {
            --primary: #059669;
            --primary-dark: #047857;
            --primary-light: #ecfdf5;
            --surface: #ffffff;
            --bg: #f8fafc;
            --text-main: #0f172a;
            --text-muted: #475569;
            --border: #e2e8f0;
            --amber-bg: #fef3c7;
            --amber-text: #92400e;
            --amber-border: #f59e0b;
        }
        * {
            box-sizing: border-box;
            margin: 0;
            padding: 0;
            font-family: 'Plus Jakarta Sans', -apple-system, BlinkMacSystemFont, sans-serif;
        }
        body {
            background-color: var(--bg);
            color: var(--text-main);
            line-height: 1.5;
            padding: 16px;
        }
        .container {
            max-width: 680px;
            margin: 0 auto;
        }
        .header {
            background: linear-gradient(135deg, #059669 0%, #065f46 100%);
            color: white;
            padding: 24px 20px;
            border-radius: 20px;
            box-shadow: 0 10px 25px -5px rgba(5, 150, 105, 0.25);
            text-align: center;
            margin-bottom: 20px;
        }
        .badge {
            display: inline-block;
            background: rgba(255, 255, 255, 0.2);
            color: #ffffff;
            padding: 4px 12px;
            border-radius: 9999px;
            font-size: 11px;
            font-weight: 700;
            letter-spacing: 0.5px;
            margin-bottom: 12px;
            text-transform: uppercase;
        }
        .header h1 {
            font-size: 22px;
            font-weight: 800;
            line-height: 1.3;
            margin-bottom: 6px;
        }
        .header p {
            font-size: 13px;
            opacity: 0.9;
        }
        .download-card {
            background: var(--surface);
            border: 2px solid #a7f3d0;
            border-radius: 18px;
            padding: 24px;
            text-align: center;
            margin-bottom: 20px;
            box-shadow: 0 4px 16px rgba(0,0,0,0.04);
        }
        .download-btn {
            display: inline-flex;
            align-items: center;
            justify-content: center;
            gap: 10px;
            background-color: var(--primary);
            color: white;
            font-size: 16px;
            font-weight: 700;
            padding: 16px 28px;
            border-radius: 14px;
            text-decoration: none;
            width: 100%;
            box-shadow: 0 6px 20px rgba(5, 150, 105, 0.35);
            transition: all 0.2s ease;
        }
        .download-btn:hover {
            background-color: var(--primary-dark);
            transform: translateY(-2px);
        }
        .info-card {
            background: var(--surface);
            border: 1px solid var(--border);
            border-radius: 16px;
            padding: 20px;
            margin-bottom: 16px;
        }
        .info-card h2 {
            font-size: 15px;
            font-weight: 700;
            color: var(--primary-dark);
            margin-bottom: 12px;
            display: flex;
            align-items: center;
            gap: 8px;
        }
        .time-box {
            background: var(--amber-bg);
            border: 1px solid var(--amber-border);
            border-radius: 12px;
            padding: 14px;
            margin-bottom: 12px;
        }
        .time-box-title {
            font-weight: 700;
            font-size: 13px;
            color: var(--amber-text);
            margin-bottom: 4px;
        }
        .time-box-desc {
            font-size: 12px;
            color: #78350f;
            line-height: 1.5;
        }
        .step-list {
            padding-left: 20px;
            font-size: 13px;
            color: var(--text-muted);
        }
        .step-list li {
            margin-bottom: 8px;
        }
        .step-list strong {
            color: var(--text-main);
        }
        .grid-2 {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 12px;
            margin-top: 10px;
        }
        .stat-item {
            background: var(--bg);
            padding: 12px;
            border-radius: 10px;
            text-align: center;
        }
        .stat-val {
            font-size: 16px;
            font-weight: 700;
            color: var(--primary);
        }
        .stat-lbl {
            font-size: 11px;
            color: var(--text-muted);
        }
        .footer {
            text-align: center;
            font-size: 11px;
            color: var(--text-muted);
            margin-top: 24px;
            padding-bottom: 16px;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <span class="badge">Aplikasi Warga & Petugas RT 06</span>
            <h1>Aplikasi Kas Jimpitan & Ronda</h1>
            <p>RT 06 RW 06 Perumahan KCVRI Berkoh, Purwokerto Selatan</p>
        </div>

        <div class="download-card">
            <h2 style="font-size: 18px; margin-bottom: 8px;">📲 Pasang Aplikasi di HP Android</h2>
            <p style="font-size: 13px; color: var(--text-muted); margin-bottom: 18px;">
                Download file APK langsung untuk dipasang di smartphone Android pengurus dan seluruh petugas ronda RT 06.
            </p>
            <a href="/download-apk" class="download-btn">
                <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"></path>
                    <polyline points="7 10 12 15 17 10"></polyline>
                    <line x1="12" y1="15" x2="12" y2="3"></line>
                </svg>
                DOWNLOAD APK ANDROID (24 MB)
            </a>
            <p style="font-size: 11px; color: var(--text-muted); margin-top: 10px;">
                Versi: v1.0 • Khusus Android 8.0 ke atas • Bebas Iklan
            </p>
        </div>

        <div class="info-card">
            <h2>⏰ Ketentuan Baru Presensi Ronda RT 06</h2>
            <div class="time-box">
                <div class="time-box-title">🔒 Akses Presensi Dibuka Pukul 00:00 - 01:00 WIB</div>
                <div class="time-box-desc">
                    Ceklist daftar hadir petugas ronda <strong>hanya dapat diakses pada pukul 00:00 sampai pukul 01:00 WIB</strong> tepat sesuai hari dan tanggal jadwal ronda. Di luar jam tersebut tombol presensi otomatis terkunci demi ketertiban jadwal jaga malam pos ronda RT 06.
                </div>
            </div>
            <div class="grid-2">
                <div class="stat-item">
                    <div class="stat-val">7 Orang</div>
                    <div class="stat-lbl">Petugas per Malam</div>
                </div>
                <div class="stat-item">
                    <div class="stat-val">Rp 20.000</div>
                    <div class="stat-lbl">Denda Tidak Hadir</div>
                </div>
            </div>
        </div>

        <div class="info-card">
            <h2>📖 Cara Memasang File APK di HP Android</h2>
            <ol class="step-list">
                <li>Klik tombol hijau <strong>"DOWNLOAD APK ANDROID"</strong> di atas.</li>
                <li>Setelah selesai diunduh, buka notifikasi atau file manager di HP dan klik <strong>Jimpitan-Ronda-RT06.apk</strong>.</li>
                <li>Jika muncul peringatan <em>"Instal aplikasi dari sumber tidak dikenal"</em>, klik <strong>Setelan</strong> lalu aktifkan <strong>Izinkan dari sumber ini</strong>.</li>
                <li>Pilih <strong>Instal</strong>. Tunggu beberapa detik hingga selesai.</li>
                <li>Buka aplikasi di HP! Seluruh data warga Dawis 1 s/d Dawis 4, kas jimpitan, dan presensi ronda siap digunakan.</li>
            </ol>
        </div>

        <div class="info-card">
            <h2>🔐 Akses Pengurus RT & Petugas</h2>
            <p style="font-size: 13px; color: var(--text-muted); margin-bottom: 8px;">
                Aplikasi ini dilengkapi <strong>Mode Warga (Lihat Saja)</strong> agar seluruh warga RT 06 dapat memantau transparansi keuangan.
            </p>
            <p style="font-size: 13px; color: var(--text-muted);">
                Untuk melakukan penginputan jimpitan, perubahan kas, dan override jadwal ronda, masukkan PIN Pengurus RT: <strong>0606</strong>.
            </p>
        </div>

        <div class="footer">
            <p>Pengurus RT 06 RW 06 Perumahan KCVRI Berkoh • Transparan, Akurat & Terbuka</p>
        </div>
    </div>
</body>
</html>
"""

class RT06Handler(http.server.BaseHTTPRequestHandler):
    def do_HEAD(self):
        self.send_response(200)
        self.send_header('Content-Type', 'text/html; charset=utf-8')
        self.end_headers()

    def do_GET(self):
        if self.path in ('/download-apk', '/download', '/app-debug.apk', '/Jimpitan-Ronda-RT06.apk'):
            apk_to_serve = APK_PATH if os.path.exists(APK_PATH) else FALLBACK_APK_PATH
            if os.path.exists(apk_to_serve):
                file_size = os.path.getsize(apk_to_serve)
                self.send_response(200)
                self.send_header('Content-Type', 'application/vnd.android.package-archive')
                self.send_header('Content-Disposition', 'attachment; filename="Jimpitan-Ronda-RT06.apk"')
                self.send_header('Content-Length', str(file_size))
                self.send_header('Access-Control-Allow-Origin', '*')
                self.end_headers()
                with open(apk_to_serve, 'rb') as f:
                    while chunk := f.read(65536):
                        self.wfile.write(chunk)
                return
            else:
                self.send_response(404)
                self.end_headers()
                self.wfile.write(b"File APK sedang dibuat, silakan muat ulang beberapa saat lagi.")
                return

        # Serve Web Portal
        self.send_response(200)
        self.send_header('Content-Type', 'text/html; charset=utf-8')
        self.send_header('Access-Control-Allow-Origin', '*')
        self.end_headers()
        self.wfile.write(HTML_CONTENT.encode('utf-8'))

    def log_message(self, format, *args):
        pass

if __name__ == '__main__':
    with socketserver.TCPServer(("", PORT), RT06Handler) as httpd:
        print(f"Serving Web Portal on port {PORT}")
        httpd.serve_forever()
