package com.example.data

import androidx.compose.ui.graphics.Color

data class SensorModel(
    val id: String,
    val name: String,
    val type: String,
    val indonesianName: String,
    val description: String,
    val fullExplanation: String,
    val workingPrinciple: String,
    val pinDefinitions: List<PinDefinition>,
    val componentsNeeded: List<String>,
    val wiringSteps: List<String>,
    val quizQuestions: List<QuizQuestion>,
    val primaryColor: Color,
    val secondaryColor: Color,
    val iconName: String
)

data class PinDefinition(
    val name: String,
    val type: String, // e.g., Power, Ground, Analog Out, Digital In, Echo, Trigger
    val arduinoConnection: String,
    val description: String
)

data class QuizQuestion(
    val id: Int,
    val question: String,
    val options: List<String>,
    val correctAnswerIndex: Int,
    val explanation: String
)

object MechatronicsData {
    val sensors = listOf(
        SensorModel(
            id = "ldr",
            name = "LDR (Light Dependent Resistor)",
            type = "Sensor Cahaya",
            indonesianName = "Sensor Intensitas Cahaya",
            description = "Sensor mekatronika analog yang mengukur intensitas cahaya sekitar dengan mengubah nilai resistansinya.",
            fullExplanation = "Light Dependent Resistor (LDR) atau Fotoresistor adalah komponen elektronik aktif yang nilai hambatan listriknya (resistansi) akan menurun seiring dengan meningkatnya intensitas cahaya yang mengenainya. LDR dibuat dari bahan semikonduktor beresistansi tinggi seperti Kadmium Sulfida (CdS). Dalam sistem mekatronika, LDR bertindak sebagai 'mata' sederhana untuk mendeteksi siang/malam, mengontrol lampu otomatis, atau mendeteksi bayangan objek pada sabuk konveyor industri.",
            workingPrinciple = "Ketika kondisi gelap (tidak ada cahaya), elektron-elektron pada bahan CdS terikat kuat, sehingga resistansi LDR berada pada tingkat maksimal (sangat tinggi, hingga beberapa Mega Ohm). Arus sulit mengalir. Sebaliknya, ketika cahaya mengenai permukaan LDR, energi foton membebaskan elektron-elektron valensi menjadi elektron bebas. Akibatnya, resistansi LDR menurun tajam (bisa sekecil beberapa ratus Ohm). Perubahan resistansi ini dibaca sebagai tegangan analog oleh mikrokontroler menggunakan metode pembagi tegangan (voltage divider).",
            pinDefinitions = listOf(
                PinDefinition("Kaki 1 (LDR)", "Analog/Input", "A0 (via Resistor 10k)", "Terminal input sensor untuk mengukur tegangan),"),
                PinDefinition("Kaki 2 (LDR)", "Power", "5V", "Terminal catu daya positif dari Arduino."),
                PinDefinition("Resistor Pin", "Ground", "GND", "Dihubungkan ke Resistor 10k Ohm yang menuju Ground (Pembagi Tegangan).")
            ),
            componentsNeeded = listOf("Arduino Uno / Nano", "Sensor LDR (CdS)", "Resistor 10k Ohm (Pull-Down)", "LED Merah/Kuning", "Resistor 220 Ohm (untuk LED)", "Kabel Jumper & Breadboard"),
            wiringSteps = listOf(
                "Hubungkan Kaki LDR 1 ke jalur 5V Arduino.",
                "Hubungkan Kaki LDR 2 ke pin Analog A0 Arduino.",
                "Hubungkan juga Kaki LDR 2 ke ground (GND) melalui Resistor 10k Ohm (metode Pembagi Tegangan).",
                "Hubungkan kaki Anoda (+) LED ke pin Digital 13 Arduino melalui Resistor 220 Ohm.",
                "Hubungkan kaki Katoda (-) LED ke pin Ground (GND) Arduino."
            ),
            quizQuestions = listOf(
                QuizQuestion(
                    1,
                    "Bagaimana sifat resistansi (hambatan) LDR saat kondisi lingkungan sekitarnya semakin TERANG?",
                    listOf("Resistansi menurun drastis", "Resistansi meningkat drastis", "Resistansi tetap stabil", "Arus listrik berhenti mengalir"),
                    0,
                    "Semakin terang cahaya yang mengenai LDR, semakin banyak elektron bebas yang terbentuk, sehingga resistansi hambatan menurun drastis dan arus mengalir lebih mudah."
                ),
                QuizQuestion(
                    2,
                    "Metode rangkaian apa yang digunakan untuk mengubah perubahan resistansi LDR menjadi perubahan tegangan yang dapat dibaca Arduino?",
                    listOf("Rangkaian Jembatan Wheatstone", "Rangkaian Pembagi Tegangan (Voltage Divider)", "Rangkaian Penyearah Gelombang", "Rangkaian Multiplexer"),
                    1,
                    "Rangkaian pembagi tegangan (dengan bantuan resistor tetap seperti 10k Ohm) diperlukan untuk mengubah perubahan resistansi LDR menjadi sinyal tegangan analog (0-5V) agar bisa dibaca pin Analog A0."
                ),
                QuizQuestion(
                    3,
                    "Pada aplikasi lampu jalan otomatis mekatronika, kapankah sinyal output mikrokontroler diatur HIGH untuk menyalakan relay/lampu?",
                    listOf("Saat nilai tegangan analog di A0 mendekati 5V (Terang)", "Saat resistansi LDR sangat kecil", "Saat nilai tegangan analog di A0 rendah / gelap (kurang dari batas threshold)", "Saat LDR dicabut dari rangkaian"),
                    2,
                    "Saat kondisi gelap, tegangan pembacaan analog menurun (jika menggunakan resistor pull-down ke gnd). Mikrokontroler mendeteksi nilai di bawah batas aman (threshold) dan mengaktifkan relay untuk menyalakan lampu."
                )
            ),
            primaryColor = Color(0xFFFFB300), // Amber
            secondaryColor = Color(0xFFFFF8E1), // Light Amber
            iconName = "wb_sunny"
        ),
        SensorModel(
            id = "ultrasonic",
            name = "HC-SR04 Ultrasonic",
            type = "Sensor Jarak",
            indonesianName = "Sensor Jarak Gelombang Suara",
            description = "Sensor mekatronika digital presisi untuk mengukur jarak benda menggunakan pantulan gelombang suara frekuensi tinggi.",
            fullExplanation = "Sensor Ultrasonik HC-SR04 adalah modul sensor pengukur jarak non-kontak yang sangat andal dan ekonomis. Sensor ini menggunakan prinsip kerja sonar yang mirip dengan sistem navigasi kelelawar atau kapal selam. Dalam industri mekatronika, sensor ultrasonik diterapkan pada robot penghindar rintangan (obstacle avoidance), sistem parkir otomatis mobil, pengukuran ketinggian cairan tangki, pembaca posisi conveyor, dan sistem deteksi volume barang.",
            workingPrinciple = "Sensor HC-SR04 memancarkan pulsa suara berfrekuensi tinggi (40 kHz) selama waktu tertentu melalui bagian pemancar (Transmitter/Trig). Gelombang suara merambat di udara bebas dengan kecepatan sekitar 340 meter per detik. Apabila gelombang menabrak suatu penghalang, akustik pantul (echo) akan memantul kembali dan ditangkap oleh bagian penerima (Receiver/Echo). Selisih waktu perjalanan pulsa pergi-pulang kemudian dikalkulasi mikrokontroler untuk menemukan jarak objek.",
            pinDefinitions = listOf(
                PinDefinition("VCC", "Power", "5V", "Catu daya positif +5V DC."),
                PinDefinition("Trig (Trigger)", "Digital Input", "Pin D9", "Input sinyal pemicu dari Arduino (kirim sinyal 10 mikrodetik untuk memulai)."),
                PinDefinition("Echo", "Digital Output", "Pin D8", "Output sinyal pantulan balik dari sensor (durasi HIGH sebanding dengan jarak)."),
                PinDefinition("GND", "Ground", "GND", "Ground catu daya.")
            ),
            componentsNeeded = listOf("Arduino Uno / Mega", "Sensor HC-SR04", "Buzzer Aktif 5V (Alarm)", "LED Merah Peringatan", "Resistor 220 Ohm", "Kabel Jumper"),
            wiringSteps = listOf(
                "Hubungkan pin VCC HC-SR04 ke pin 5V Arduino.",
                "Hubungkan pin GND HC-SR04 ke pin Ground (GND) Arduino.",
                "Hubungkan pin Trig HC-SR04 ke pin Digital D9 Arduino.",
                "Hubungkan pin Echo HC-SR04 ke pin Digital D8 Arduino.",
                "Hubungkan kaki positif (+) Buzzer ke pin Digital D10 Arduino.",
                "Hubungkan kaki negatif (-) Buzzer ke pin GND Arduino."
            ),
            quizQuestions = listOf(
                QuizQuestion(
                    1,
                    "Pin manakah pada modul sensor HC-SR04 yang berfungsi untuk mengirimkan sinyal pemicu transmisi gelombang suara?",
                    listOf("Echo Pin", "Trigger (Trig) Pin", "VCC Pin", "Out Pin"),
                    1,
                    "Trigger (Trig) Pin berfungsi menerima pulsa HIGH selama 10 mikrodetik dari Arduino untuk memerintahkan sensor memancarkan gelombang ultrasonik."
                ),
                QuizQuestion(
                    2,
                    "Berapakah frekuensi gelombang suara ultrasonik yang dirilis oleh sensor HC-SR04 ketika mendeteksi objek?",
                    listOf("20 Hz", "440 Hz", "40 kHz", "2.4 GHz"),
                    2,
                    "HC-SR04 memancarkan suara pada frekuensi tinggi 40 kHz (Ultrasonik). Frekuensi ini berada jauh di atas rentang pendengaran manusia (20 Hz - 20 kHz)."
                ),
                QuizQuestion(
                    3,
                    "Bagaimana rumus dasar yang digunakan Arduino untuk mengkonversi durasi waktu pulsa Echo (mikrodetik) menjadi Jarak (cm)?",
                    listOf("Jarak = (Waktu × 0.034) / 2", "Jarak = Waktu × 340", "Jarak = Waktu / 9.8", "Jarak = Waktu² × Kecepatan Cahaya"),
                    0,
                    "Kecepatan suara di udara adalah ~0.034 cm/µs. Karena gelombang menempuh perjalanan bolak-balik (pergi dan pulang), maka durasi waktu harus dibagi 2 sebelum dikalikan kecepatan suara."
                )
            ),
            primaryColor = Color(0xFF0288D1), // Deep Light Blue
            secondaryColor = Color(0xFFE1F5FE), // Light Blue
            iconName = "settings_input_antenna"
        ),
        SensorModel(
            id = "pir",
            name = "PIR (Passive Infrared)",
            type = "Sensor Gerak",
            indonesianName = "Sensor Deteksi Gerakan Tubuh",
            description = "Sensor mekatronika digital yang mendeteksi pancaran radiasi inframerah dari mahluk hidup untuk sistem keamanan otomatis.",
            fullExplanation = "Passive Infrared (PIR) adalah sensor elektro-optik digital yang mendeteksi pancaran radiasi inframerah yang dilepaskan secara alami oleh tubuh manusia atau hewan. Kata 'Passive' merujuk pada sifat sensor yang sama sekali tidak memancarkan energi (sinar inframerah) sendiri, melainkan hanya mendeteksi radiasi pasif eksternal yang melintas di depannya. Di bidang vokasional mekatronika, sensor ini sering diaplikasikan pada sistem alarm antipencuri, sakelar lampu toilet otomatis, pintu mal otomatis, dan sistem hemat daya gedung.",
            workingPrinciple = "Sensor PIR memiliki elemen piroelektrik (kristal tipis yang melepas muatan listrik ketika terkena inframerah). Lensa Fresnel cembung di bagian luar membagi area sensor menjadi beberapa zona deteksi. Ketika manusia (pembawa suhu hangat inframerah) bergerak melewati satu zona ke zona lainnya, sensor mendeteksi perubahan mendadak fluktuasi energi inframerah yang jatuh pada elemen piroelektriknya. Hal ini memicu rangkaian internal melontarkan sinyal HIGH (+3.3V) pada pin output selama jangka waktu tertentu.",
            pinDefinitions = listOf(
                PinDefinition("VCC (+ / 5V)", "Power", "5V", "Catu daya positif +5V DC."),
                PinDefinition("OUT (Output)", "Digital Output", "Pin D2", "Mengirimkan sinyal HIGH (3.3V) jika mendeteksi gerakan, LOW jika diam."),
                PinDefinition("GND (-)", "Ground", "GND", "Ground catu daya.")
            ),
            componentsNeeded = listOf("Arduino Uno", "Sensor PIR (HC-SR501)", "Relay Module 5V", "Lampu AC / DC (Simulasi)", "Kabel Jumper"),
            wiringSteps = listOf(
                "Hubungkan pin VCC (+) PIR ke pin 5V Arduino.",
                "Hubungkan pin OUT (tengah) PIR ke pin Digital D2 Arduino.",
                "Hubungkan pin GND (-) PIR ke pin Ground (GND) Arduino.",
                "Hubungkan INPUT pin modul Relay ke pin Digital D3 Arduino.",
                "Hubungkan pin VCC & GND Relay ke 5V & GND Arduino.",
                "Hubungkan jalur lampu AC / DC ke kontak NO (Normally Open) dan COM pada terminal Output Relay."
            ),
            quizQuestions = listOf(
                QuizQuestion(
                    1,
                    "Mengapa sensor PIR dinamakan sensor 'Pasif' (Passive)?",
                    listOf(
                        "Karena tidak memancarkan energi inframerah sendiri, melainkan hanya menerima radiasi",
                        "Karena hanya mendeteksi objek diam",
                        "Karena tidak membutuhkan catu daya listrik",
                        "Karena menggunakan kabel yang sangat sedikit"
                    ),
                    0,
                    "PIR bersifat pasif penuh karena tidak merilis sinar inframerah melainkan hanya memonitor dan menyerap radiasi inframerah yang dipancarkan secara alami oleh lingkungan sekitar/tubuh manusia."
                ),
                QuizQuestion(
                    2,
                    "Komponen kubah plastik putih cembung bergerigi yang membungkus sensor PIR berfungsi sebagai apa?",
                    listOf("Lensa Fresnel untuk memfokuskan inframerah", "Pelindung sensor dari benturan fisik", "Filter warna agar sensor terlihat elegan", "Antena penguat sinyal Wi-Fi"),
                    0,
                    "Kubah plastik putih tersebut adalah Lensa Fresnel. Fungsinya membagi bidang pandang sensor menjadi zona-zona mikro dan memfokuskan radiasi inframerah dari berbagai arah langsung ke sensor piroelektrik."
                ),
                QuizQuestion(
                    3,
                    "Apakah jenis sinyal output yang diberikan oleh pin OUT sensor PIR saat mendeteksi adanya gerakan manusia?",
                    listOf("Sinyal Analog kontinu (0V - 5V)", "Sinyal Digital biner (HIGH 3.3V / LOW 0V)", "Sinyal Frekuensi tinggi PWM", "Sinyal Serial ASCII"),
                    1,
                    "Sensor PIR memberikan keluaran digital diskrit yang sangat mudah diproses: Sinyal HIGH (3.3V) jika terdeteksi gerakan, dan sinyal LOW (0V) jika lingkungan tenang/gerakan selesai."
                )
            ),
            primaryColor = Color(0xFFD81B60), // Hot Pink/Magenta
            secondaryColor = Color(0xFFFCE4EC), // Light Pink
            iconName = "directions_run"
        ),
        SensorModel(
            id = "dht11",
            name = "DHT11 Temp & Humidity",
            type = "Sensor Suhu Klima",
            indonesianName = "Sensor Suhu & Kelembaban Udara",
            description = "Sensor mekatronika digital multifungsi terintegrasi yang mendeteksi perubahan suhu udara dan tingkat kelembaban secara bersamaan.",
            fullExplanation = "Sensor DHT11 adalah modul sensor digital gabungan yang mampu mengukur suhu (temperatur) dan kelembaban udara (humidity) sekitarnya. Modul ini memiliki kalibrasi instruksi koefisien keluaran digital yang sangat akurat. Di dalam sistem otomasi dan mekatronika, DHT11 digunakan secara meluas pada stasiun cuaca pintar (smart weather station), pengendali pendingin AC ruangan, kontrol suhu inkubator telur otomatis, rumah pintar (IoT smart home), dan rumah kaca bercocok tanam (smart greenhouse).",
            workingPrinciple = "DHT11 terdiri dari sensor kelembaban kapasitif dan sebuah termistor NTC (Negative Temperature Coefficient) untuk mendeteksi suhu. Sensor kelembaban bekerja mengukur konduktivitas listrik antara elektroda polimer fungsional sensitif uap air. Termistor NTC mengubah perubahan suhu menjadi perubahan resistansi (resistansi mengecil saat suhu naik). Modul DHT11 memiliki chip mikrokontroler internal 8-bit yang mengubah pembacaan analog sensori tersebut menjadi transmisi data digital satu jalur (Single-Wire serial bus) ke Arduino.",
            pinDefinitions = listOf(
                PinDefinition("VCC", "Power", "5V", "Catu daya positif +5V (mendukung 3.3V - 5V)."),
                PinDefinition("DATA", "Digital Input/Output", "Pin D4", "Koneksi serial digital satu jalur untuk berkirim data suhu & kelembaban."),
                PinDefinition("NC (Not Connected)", "No Pin", "Tidak terhubung", "Pin kosong fungsional, tidak dipakai."),
                PinDefinition("GND", "Ground", "GND", "Ground catu daya.")
            ),
            componentsNeeded = listOf("Arduino Uno / Nano", "Modul Sensor DHT11", "Modul LCD 16x2 I2C Display", "Kipas DC 5V (Cooling Fan)", "Transistor TIP120 (Driver Kipas)", "Kabel Jumper"),
            wiringSteps = listOf(
                "Hubungkan pin VCC DHT11 ke pin 5V Arduino.",
                "Hubungkan pin GND DHT11 ke pin Ground (GND) Arduino.",
                "Hubungkan pin DATA DHT11 ke pin Digital D4 Arduino.",
                "Hubungkan pin SDA & SCL LCD I2C ke pin SDA/A4 & SCL/A5 Arduino.",
                "Hubungkan pin VCC & GND LCD ke 5V & GND Arduino.",
                "Pasang basis Transistor ke pin D5 Arduino, kolektor ke terminal negatif Kipas, emitor ke Ground, dan terminal positif Kipas langsung ke 5V (Meka Driver)."
            ),
            quizQuestions = listOf(
                QuizQuestion(
                    1,
                    "Komponen jenis apa yang digunakan di dalam modul DHT11 khusus untuk mendeteksi perubahan suhu (temperatur)?",
                    listOf("Termistor NTC (Negative Temperature Coefficient)", "Komponen Termokopel tipe K", "Photodiode Sensitif Cahaya", "Kondensator Taraf Tinggi"),
                    0,
                    "DHT11 memakai termistor NTC (hambatan termal negatif) untuk mendeteksi suhu ruangan. Nilai resistansinya berbanding terbalik dengan perubahan suhu sekeliling."
                ),
                QuizQuestion(
                    2,
                    "Metode pengiriman data apa yang dianut oleh sensor DHT11 untuk mentransfer informasi suhu dan kelembaban ke Arduino?",
                    listOf("Komunikasi serial satu kabel (Single-Wire Serial Bus)", "Protokol I2C (SDA-SCL)", "Metode Tegangan Analog (0V - 5V)", "Komunikasi data paralel 8-bit"),
                    0,
                    "Modul DHT11 menggunakan bus serial khusus satu-kabel (Single-Wire) berpemilik yang mengirimkan paket data 40-bit lengkap berisi status kelembaban, suhu, dan checksum verifikasi kesalahan."
                ),
                QuizQuestion(
                    3,
                    "Jika kelembaban udara relatif (Humidity) bernilai 90% pada sensor DHT11, apa arti indikasi fungsional tersebut bagi lingkungan?",
                    listOf("Kondisi udara berair/sangat basah atau berkabut", "Kondisi udara gersang/sangat kering", "Kondisi badai matahari magnetik", "Kondisi hampa udara luar angkasa"),
                    0,
                    "Kelembaban diukur dalam persentase Relative Humidity (%RH). Nilai 90% RH menunjukkan kadar uap air di udara sangat jenuh (sangat tinggi/basah), biasanya terjadi saat hujan lebat, kabut, atau daerah berair tinggi."
                )
            ),
            primaryColor = Color(0xFF00897B), // Deep Teal
            secondaryColor = Color(0xFFE0F2F1), // Light Teal
            iconName = "thermostat"
        )
    )
}
