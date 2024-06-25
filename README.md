# 🎬 StreamSpace - Your Gateway to Entertainment 🎬🎉

[![Java CI with Maven](https://github.com/AkshathSai/StreamSpace/actions/workflows/ci-build.yml/badge.svg?branch=main)](https://github.com/AkshathSai/StreamSpace/actions/workflows/ci-build.yml)
![Maven](https://badgen.net/badge/icon/maven?icon=maven&label)
![GitHub](https://img.shields.io/github/license/akshathsai/TuneTrip)
> [!CAUTION]
> The StreamSpace project is designed and developed solely for educational purposes. It demonstrates the capabilities of modern software technologies in aggregating and streaming media content. The use of this software for downloading copyrighted material without permission is strictly prohibited and against the law. Users are responsible for complying with all applicable laws and regulations in their jurisdiction regarding copyright infringement. We strongly advise against using StreamSpace for any activities that may infringe upon the rights of content creators and distributors. Use this software responsibly and at your own risk.

Ready to explore the world of StreamSpace? [Dive right in!](#setup--installation)

<p align="center">
<img src="https://upload.wikimedia.org/wikipedia/de/e/e1/Java-Logo.svg" alt="Java">
<img src="https://github.com/AkshathSai/CinemaPass/assets/89769614/57bd7d6f-871c-4c22-adb8-9fdf27728f30" width="200" height="200" alt="Spring">
</p>

## Contents

- [Screenshots](#screenshots) 
- [The Challenge](#the-challenge)
- [Our Solution](#solution)
- [Keynotes](#key-features)
- [Technologies Used](#technologies-used)
- [Setup & Installation](#setup--installation)
- [Contribute](#contribute)
- [Bug Reports](#bug-reports)
- [License](#license)

## Screenshots

### Simple Straight forward call-to-action UI 

<img width="1512" alt="Homapage UI shot" src="https://github.com/AkshathSai/StreamSpace/assets/89769614/a9c000e9-4f9c-4822-acf1-2b4a24938527">

### Lightning Fast⚡ Active Search 💨(Type ahead suggestions Sweet 🥹)

<img width="1512" alt="Active Search demo shot" src="https://github.com/AkshathSai/StreamSpace/assets/89769614/8b801bd1-f498-4b31-afdd-a17978d16d68">

### Instant movie Details, stream uncompressed eye-candy 🥹 upto 4K Quality!!
<img width="1512" alt="Movie Streaming demo shot" src="https://github.com/AkshathSai/StreamSpace/assets/89769614/9e765ad5-00c2-4245-ac10-e0057de282ca">

### Not sure? Watch Trailers😃

<img width="1512" alt="Youtube Trailers demo shot" src="https://github.com/AkshathSai/StreamSpace/assets/89769614/a6bf8775-aa4a-4319-b0d0-966bd88867d0">

### Download Movies

<img width="1512" alt="Screenshot 2024-05-13 at 1 09 07 PM" src="https://github.com/AkshathSai/StreamSpace/assets/89769614/df4306d7-3315-4ef2-bcf4-468e9a718bee">

### Get Suggested & Related Movies 

<img width="1512" alt="Suggested Movies Screenshot" src="https://github.com/AkshathSai/StreamSpace/assets/89769614/ad8ba5ae-75a9-45da-aa0b-e4a85a1f424e">

### View All Page with Pagination

<img width="1512" alt="View All Page Screenshot-1" src="https://github.com/AkshathSai/StreamSpace/assets/89769614/88190f52-3394-4311-bd6b-05c1d315ec0e">

<img width="1512" alt="View All Page Screenshot-2" src="https://github.com/AkshathSai/StreamSpace/assets/89769614/6587d8ab-fe4a-436a-b8b4-668072da3ffe">

### And Finally, Dark theme 🤯 

<img width="1512" alt="Screenshot 2024-05-30 at 10 48 49 PM" src="https://github.com/AkshathSai/StreamSpace/assets/89769614/b8287d24-0762-4a9b-a7c5-033dd4d594db">


## The Challenge

In today's digital age, entertainment options have exploded, with countless OTT platforms vying for your attention. However, the more services that emerge, the more you find yourself juggling subscriptions just to access the content you love. It's a constant battle between your wallet and your desire to watch that latest blockbuster movie, explore a new TV series, or listen to your favorite tunes.

## Solution

StreamSpace is here to liberate you from subscription overload and bring all your entertainment needs under one unified banner. We believe that everyone should have the freedom to enjoy a wide variety of content without breaking the bank. With StreamSpace, you get:

1. All-in-One Access
2. Virtually Unlimited Choices
3. Seamless Experience

## Keynotes

- 🔍 Search for content based on Movie Title/IMDb Code, Actor Name/IMDb Code, Director Name/IMDb Code
- 🎞️ Stream movies while downloading in parallel for free!
- [Coming soon] 💾 In-memory streaming without downloading 
- [Future Plan] 🎵 Torrent Tunes  - Your favorite Music/Tunes delivered over P2P network
- I'm pumped to collaborate and grow together in this open-source journey. [Let's connect! 🤝](mailto:akshathsai.pittala@outlook.com?)


## Technologies Used

- Java (JDK 21)
- Spring Boot
- Thymeleaf
- HTML
- CSS
- H2 Database
- Spring Data JPA/Hibernate
- JavaScript
- HTMX
- [Bt library](https://github.com/atomashpolskiy/bt)

## Setup & Installation

1. Download [CloudFlare 1.1.1.1 VPN](https://1.1.1.1) & [JDK 21](https://adoptium.net/temurin/releases/)

2. Clone the repository:

```
git clone https://github.com/AkshathSai/StreamSpace.git
```

3. Change into the project directory:
   
```
cd StreamSpace
```

4. Build the project:
```
mvn clean install
```
5. Run the project:
```
cd target
```
```
java -jar StreamSpace-0.0.1-SNAPSHOT.jar
```
6. Visit https://localhost:8080/
7. Check out API Docs at https://localhost:8080/swagger-ui/index.html

## Contribute

We welcome contributions! To contribute, follow these steps:

1. Create a new branch.
2. Make your changes.
3. Open a pull request.

Please ensure your pull request adheres to coding conventions and includes appropriate tests.

## Bug Reports

Encountered an issue? Please [open a new issue](https://github.com/AkshathSai/StreamSpace/issues) and provide detailed information about the problem.




## License

The MIT License (MIT) - author: Akshath Sai, Pittala akshathsai.pittala@outlook.com

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

## Disclaimer

THIS IS AN ALPHA QUALITY SOFTWARE FOR RESEARCH PURPOSES ONLY. THIS IS NOT A FINISHED PRODUCT. YOU ARE RESPONSIBLE FOR COMPLYING WITH LOCAL LAWS AND REGULATIONS. NO WARRANTY EXPRESSED OR IMPLIED.

[^1]: This project is the brainchild of a visionary programmer, a true GOAT (Greatest of All Time) in the world of code, who believes in the magical power of technology to reshape how we experience movies, music, and more. 🌟
