# 📺 Stream It
A full-stack video streaming platform built in Java. StreamIt is secured with TLS/SSL, includes adaptive streaming, load balancing, and a responsive user interface.

## 💻 Demonstration
Although the application was designed for wireless communication, with LAN connections successfully tested, it is recommended to run both the client and server locally.

![Demonstration](ReadMeAssets/Animation.gif)

## 🚀 How to Use
After cloning this repository, navigate to the subfolder of the project you want to run. It is recommended to have a terminal open for both projects.

To run each project, use:

```bash
./gradlew run
```

### ⚙️ Prerequisites
In order for the project to run successfully on both sides, make sure you have:

- VLC Media Player installed on the client side.
- Java 21 installed on both systems.
- MySQL installed on the server side.

### 📦 Packaging
To package the client project into an installable application, use one of the following commands:
```bash
./gradlew clean build jpackageLinux
```
or
```bash
./gradlew clean build jpackageWindows
```

The application is fully compatible with both operating systems.

### ⚠️ Important
- Make sure that `ffmpeg` is installed on the server side.
  If the server is running on Windows, ensure that the required environment variables are properly configured.

- Any video placed under:

    ```text
    Stream-It\StreamItServer\VideosAndPictures\AvailableVideos
    ```

    must follow the naming convention:
    ```text
    "x"-"y"p."z"
    ```

    where:
    - `x` is the video name.
    - `y` is the video resolution.
    - `z` is the file extension.

    Example:
    ```text
    SurfingS1Ep1-240p.avi
    ```

- After the first launch, a folder will be created in the user's directory containing the configuration file. This file allows the user to edit the server IP address and port.

- Make sure that MySQL has:

    ### The Database User

    Create the required account:

    ```sql
    Create User 'StreamItAcc'@'localhost' Identified BY 'r{CX70@1+i!-';
    ```

    ### The Database

    Create the required database:

    ```sql
    Create Database StreamIt;
    ```

    ### Permissions

    Grant the required privileges:

    ```sql
    Grant All Privileges On StreamIt.* To 'StreamItAcc'@'localhost';

    Flush Privileges;
    ```

- Make sure that you add an environment file in the server project named:
    ```text
    Variables.env
    ```

    Additionally, ensure that the following values are configured:

    ```bash
    KEYSTORE_PASSWORD=password

    JWT_KEY='Some other password'
    ```

    `JWT_KEY` is only significant within your respective database.
    
    While `KEYSTORE_PASSWORD` is required for the encryption functionality. Therefore, creating your own RSA-encrypted keys is highly recommended.

## 🤝 Contributing

Contributions and suggestions are welcome!

To contribute:
1. Fork the repo
2. Create a branch: `git checkout -b feature/your-idea`
3. Commit your changes: `git commit -m "Add your idea"`
4. Push and open a pull request
