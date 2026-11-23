# Ravi AI — Android starter

यह Motorola Edge 50 Pro / Android के लिए शुरुआती Kotlin प्रोजेक्ट है।

## अभी क्या काम करता है
- माइक बटन दबाकर हिंदी voice input
- Hindi text-to-speech (फोन में उपलब्ध Hindi voice)
- WhatsApp, Instagram, YouTube और Chrome खोलना
- नंबर बोलने पर Dialer खोलना (कॉल करने से पहले आपको Call दबाना होगा)
- समय बताना
- Clock app में alarm सेट करने का अनुरोध
- Android Settings खोलना
- शेयर शीट से मैसेज तैयार करना; भेजने से पहले ऐप/व्यक्ति आप चुनते हैं

## क्या अभी शामिल नहीं है
- हमेशा background में सुनना / custom “Hey Ravi” wake word
- अपने आप WhatsApp भेजना या कॉल करना
- ChatGPT-जैसी ऑनलाइन AI बातचीत
- ऐप का बना हुआ APK

## Android Studio में चलाने का तरीका
1. Android Studio में इस ZIP को Extract करके `RaviAI_Android_Starter` फ़ोल्डर खोलें।
2. Gradle Sync पूरा होने दें (Android SDK 35 और JDK 17/Android Studio compatible setup चाहिए)।
3. Motorola फोन में Developer options और USB debugging चालू करें, USB से जोड़ें।
4. Run दबाएँ। पहली बार माइक्रोफोन permission माँगे तो Allow करें।
5. फोन में Hindi speech recognition और Hindi Text-to-Speech voice उपलब्ध/इंस्टॉल होनी चाहिए।

## जरूरी सीमाएँ
Android का SpeechRecognizer आम तौर पर लगातार background listening के लिए नहीं है और पहचान के लिए ऑडियो remote service पर भेज सकता है। इसीलिए यह starter push-to-talk रखता है। Female voice की उपलब्धता फोन के installed TTS engine/voice पर निर्भर करती है; यदि female Hindi voice उपलब्ध न हो तो Android Text-to-Speech settings में voice डाउनलोड/चुनें।

## सुरक्षा
यह starter कोई API key नहीं माँगता और कोई संदेश/कॉल अपने आप नहीं भेजता। AI chat जोड़ने के लिए सुरक्षित backend और चुनी हुई AI service की API key अलग से configure करनी होगी। API key को Android app में सीधे hard-code न करें।

## Female Ravi AI Live Wallpaper
प्रोजेक्ट में `Ravi AI Female Live Wallpaper` सेवा जोड़ी गई है। Build/Install के बाद Android के Wallpaper picker में Live Wallpapers के अंतर्गत इसे चुनें। अभी यह generated female AI scene को लाइव-वॉलपेपर सेवा के माध्यम से दिखाता है; यह वास्तविक बोलती/चलती 3D लड़की नहीं है। Voice assistant अलग से ऐप के माइक्रोफोन बटन से चलता है।


## GitHub Actions से APK बनाना (मोबाइल से)
1. इस ZIP को Extract करें।
2. इसकी अंदर की सभी फाइलें और `.github` hidden folder सहित repository के root में upload करें। ZIP को repository में रखने भर से build नहीं होगा।
3. GitHub → Actions खोलें। `Build Ravi AI APK` workflow चलना चाहिए।
4. सफल run खोलें → Artifacts → `RaviAI-debug-apk` डाउनलोड करें।
5. ZIP artifact extract करके `app-debug.apk` install करें।

इस build में voice assistant push-to-talk है। Female TTS आवाज़ फोन में उपलब्ध Hindi voice पर निर्भर करती है। Live wallpaper अभी static image को wallpaper service से दिखाता है; facial animation/AI conversation और always-on wake word शामिल नहीं हैं।
