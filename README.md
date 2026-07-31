# MedColleague - AI Clinical Educator & Board Exam Prep (PWA & Web App)

MedColleague is a cross-platform AI-powered medical education platform, clinical decision aid, and board exam question bank with interactive AI Video & Audio tutoring.

---

## 🚀 How to Publish for FREE on Vercel via GitHub (No Play Store / Console Fee)

You can publish and host this application completely free as a **Progressive Web App (PWA)** and web application without paying any $25 Google Play Console developer fee.

### Step 1: Push to GitHub
1. In Google AI Studio, click **Settings / Share / Export** in the top-right toolbar.
2. Select **Push to GitHub** (or **Export ZIP** and upload to your GitHub account).
3. Connect your GitHub repository.

### Step 2: Deploy to Vercel (100% Free)
1. Go to [Vercel.com](https://vercel.com) and log in with your GitHub account.
2. Click **Add New...** -> **Project**.
3. Import your `MedColleague` GitHub repository.
4. Vercel will automatically detect `vercel.json` and deploy your app instantly!
5. Your app will be live at `https://your-project.vercel.app` with full PWA installability on mobile (iOS & Android) and desktop (macOS & Windows).

---

## ⚡ Automated Deployment via GitHub Actions
This project includes a pre-configured GitHub Actions workflow in `.github/workflows/deploy.yml`. 

Whenever you push changes to the `main` or `master` branch on GitHub:
- The workflow automatically triggers.
- It builds and deploys the latest version directly to Vercel.

---

## 📱 PWA Features
- **Installable**: Adds directly to home screen on iOS, Android, macOS, and Windows.
- **Offline Support**: Caches core assets via `public/sw.js` service worker.
- **Interactive AI Video & Audio Tutor**: Real-time voice and video tutoring with Dr. Maya for uploaded medical PDFs and exam questions.
