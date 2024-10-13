<h1 align="center" style="font-weight: bold;">SyncTok API 💻</h1>

<p align="center">
<a href="#technologies">Technologies</a> •
<a href="#started">Getting Started</a> •
<a href="#routes">API Endpoints</a> •
<a href="#contribute">Contribute</a>
</p>

<p align="center">Welcome to SyncTok API – a Spring Boot application designed to streamline the process of publishing short-form video content across multiple social media platforms. With SyncTok, you can upload once and publish to TikTok, Instagram, and YouTube simultaneously.</p>

<h2 id="technologies">💻 Technologies</h2>

- Java 22
- Spring Boot 3.2.3
- Spring Web
- Maven
- Cloudinary API
- TikTok API
- Instagram API
- YouTube API
- JSON Library (org.json)
- dotenv-java for environment variable management
- JUnit and Spring Boot Test for testing

<h2 id="started">🚀 Getting Started</h2>

Here's how to set up SyncTok API locally.

<h3>Prerequisites</h3>

- [Java Development Kit (JDK) 22](https://www.oracle.com/java/technologies/javase/jdk22-archive-downloads.html)
- [Maven](https://maven.apache.org/download.cgi)
- [Git](https://git-scm.com/downloads)
- [Cloudinary account](https://cloudinary.com/)
- API credentials for [TikTok](https://developers.tiktok.com/), [Instagram](https://developers.facebook.com/docs/graph-api/), and [YouTube](https://developers.google.com/youtube)

<h3>Cloning</h3>

```bash
git clone https://github.com/tiagoaalves/synctok-api.git
cd synctok-api
```

<h3>Config .env variables</h3>

Create a `.env` file in the project root from the `.env.example` with your API credentials:

```yaml
CLOUDINARY_CLOUD_NAME=your_cloud_name
CLOUDINARY_API_KEY=your_api_key
CLOUDINARY_API_SECRET=your_api_secret
TIKTOK_ACCESS_TOKEN=your_tiktok_access_token
INSTAGRAM_ACCESS_TOKEN=your_instagram_access_token
INSTAGRAM_ACCOUNT_ID=your_instagram_account_id
YOUTUBE_ACCESS_TOKEN=your_youtube_access_token
```

<h3>Starting</h3>

To build and run the project:

```bash
./mvnw clean install
./mvnw spring-boot:run
```

The API will be available at `http://localhost:8080`.

<h2 id="routes">📍 API Endpoints</h2>

| Route | Description |
|-------|-------------|
| <kbd>POST /api/v1/video/publish</kbd> | Publish a video to specified platforms |

<h3>POST /api/v1/video/publish</h3>

**REQUEST**

Multipart form data:
- `video`: The video file to upload
- `platforms`: Comma-separated list of platforms (e.g., "tiktok,instagram,youtube")

**RESPONSE**

```json
{
  "message": "Video successfully uploaded and published to TikTok, Instagram, YouTube",
  "videoUrls": {
    "tiktok": "https://www.tiktok.com/@user/video/1234567890",
    "instagram": "https://www.instagram.com/p/abcdefghij/",
    "youtube": "https://youtu.be/dQw4w9WgXcQ"
  }
}
```

<h3>Documentations that might help</h3>
This project does not implement the OAuth flow for any of the APIs. You will need to obtain the access tokens and account IDs from the respective platforms and provide them in the `.env` file. 
However, here's the [postman collection](https://github.com/tiagoaalves/synctok-api/blob/main/docs/postman-collection.json) I built while doing this, it might be helpful.

<h3>Running Tests</h3>

This project uses JUnit and Spring Boot Test for testing. To run the tests, use:

```bash
./mvnw test
```

<h3>Social Media Integrations</h3>

The integrations with TikTok, Instagram, and YouTube are implemented using custom clients that interact with each platform's API. These clients are not part of external libraries but are custom implementations within the project.

Thank you for checking out SyncTok API. I hope this project simplifies your multi-platform video publishing workflow!
