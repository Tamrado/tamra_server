


<br />

  <h1 align="center">TAMRA</h1>

  <p align="center">
    Social Network Service for everyone <br/>
	<br/>
with Spring Boot
    <br />
   </p>

&nbsp;
<!-- TABLE OF CONTENTS -->
## Table of Contents

* [About the Project](#about-the-project)
  * [Built With](#built-with)
  * [Dependencies for Server](#dependencies-for-server)
* [Getting Started](#getting-started)
  * [Prerequisites](#prerequisites)
  * [Java](#java)
  * [Start Tamra locally](#start-tamra-locally)
* [Details](#details) 
  * [Features](#features) 
  * [Deploy](#deploy)
  * [Document](#document)
  * [Demo](#demo)
 * [Contact](#contact)

&nbsp;
<!-- ABOUT THE PROJECT -->
## About The Project

![IMG_3790](https://user-images.githubusercontent.com/45280737/72733511-5816fe80-3bdb-11ea-9b09-910683a29735.png)

<div align="center">
<br/>
  <p align="center"><b>모두가 편리하게 사용할 SNS 서비스, 탐라 Tamra</b></p>
</div>


&nbsp;
### Built With

*  [Spring Boot](https://github.com/spring-projects/spring-boot) 2.1.7 RELEASE
*  [AWS RDS](https://aws.amazon.com/ko/rds/)
*  [AWS S3](https://aws.amazon.com/ko/s3/)
*  [Tomcat](http://tomcat.apache.org/)
* Spring Security
* Kakao api
* Spring batch/ Quartz 
* [Lombok](https://projectlombok.org/)
* front repo : [https://github.com/jeongdaeun98/timeline_front](https://github.com/jeongdaeun98/timeline_front)

&nbsp;
### Dependencies for Server
```
dependencies {    
	implementation 'org.springframework.boot:spring-boot-starter-web'  
	implementation 'org.springframework.boot:spring-boot-starter-web-services'  
	compile 'commons-io:commons-io:2.6'    
	compile group: 'com.google.guava', name: 'guava', version: '28.0-jre'  
	compile group: 'org.javassist', name: 'javassist', version: '3.25.0-GA'  
	compile group: 'commons-fileupload', name: 'commons-fileupload', version: '1.4'  
	compile group: 'com.amazonaws', name: 'aws-java-sdk', version: '1.11.602'  
	compile fileTree(dir: 'ext_libs', include: ['*.jar'])    
	compile 'org.springframework.boot:spring-boot-starter-thymeleaf'  
	annotationProcessor 'org.springframework.boot:spring-boot-configuration-processor'  
	compile('org.springframework.boot:spring-boot-starter-batch')  
        compile group: 'org.quartz-scheduler', name: 'quartz', version: '2.3.2'  
	compile group: 'org.springframework', name: 'spring-context-support', version: '5.2.1.RELEASE'  
	compile('org.springframework.boot:spring-boot-configuration-processor')  
        implementation 'com.google.code.gson:gson:2.8.6'

	//db connection
	compile group: 'org.apache.tomcat', name: 'tomcat-jdbc', version: '7.0.19'  
	implementation 'org.springframework.boot:spring-boot-starter-data-jpa'  
	implementation 'org.springframework.boot:spring-boot-starter-jdbc'
	implementation 'org.springframework.session:spring-session-jdbc'  
	runtimeOnly 'mysql:mysql-connector-java'    
	compile group: 'mysql', name: 'mysql-connector-java', version: '8.0.16'  
	compile group: 'com.zaxxer', name: 'HikariCP', version: '3.3.1'
	
	//lombok
	annotationProcessor 'org.projectlombok:lombok'  
	compile 'org.projectlombok:lombok'

	//authentication
	compile 'org.springframework.boot:spring-boot-starter-security'  
	compile group: 'org.springframework.security', name: 'spring-security-web', version: '5.1.5.RELEASE'  
	compile 'org.springframework.security:spring-security-config:5.1.5.RELEASE'  
	compile 'io.jsonwebtoken:jjwt:0.9.0'  
      
        // document
        implementation 'io.springfox:springfox-swagger2:2.9.2'  
	implementation 'io.springfox:springfox-swagger-ui:2.9.2' 

	//test
	testImplementation 'org.springframework.boot:spring-boot-starter-test'  
	testImplementation 'io.projectreactor:reactor-test'
}
```

&nbsp;
<!-- GETTING STARTED -->
## Getting Started



### Prerequisites

For development, you will only need jdk-1.8

### Java

- #### Openjdk installation on Mac

	1. Just go on [official adopt-openjdk website]([https://adoptopenjdk.net/](https://adoptopenjdk.net/)) and download the installer.


	2. You can install it easily with [homebrew](https://brew.sh/index_ko).
	```
	$ brew tap AdoptOpenJDK/openjdk
	$ brew cask install adoptopenjdk8
	```
&nbsp;
- #### Check your java version 

  If the installation was successful, you should be able to get like this.

	```
	$ java -version

	java version "1.8.0_221"
	Java(TM) SE Runtime Environment (build 1.8.0_221-b11)
	Java HotSpot(TM) 64-Bit Server VM (build 25.221-b11, mixed mode)
	```

&nbsp;
### Start Tamra locally

1. Clone the repo
```sh
$ git clone https://github.com/juhyeon96/Sns-timeline.git
```
2. Move to project directory
```sh
$ cd Sns-timeline
```
3. Build without test : gradle
```
$ ./gradlew build -x test
```
4. Run Tamra
```
$ java -jar build/libs/*.jar
```
&nbsp;
## Details 

### Features
0. spring security filter
	- check accesstoken / kakao accesstoken
	- renew accesstoken / kakao refreshtoken and accesstoken
1. membership 
	- sign up/ in/ out
	- kakao-membership
	- upload/edit user image
	- edit user info
	- validation
	- manage alarm
	- search user
2. follow
	- follow
	- unfollow
	- alarm
	- search friend list
3. post
	- post create/ read/ update/ delete
	- manage show level of each post
	- upload/ delete images
	- comment create/ read/ update/ delete
	- like/ cancel like to post
	- list liked-user to post
	- tag friends
	- alarm : about tag
4. newsfeed
	- get user-profile
		- total post
		- total followers
		- total following
		- post list
	- main page
		- my posts
		- friends' posts for followers/public
		- my news of like/ comment
		- friends' news of like/ comment

### Deploy
* AWS EC2 
* <b>Link : [http://tamra.site/](http://tamra.site/)</b>
&nbsp;
### Document
1. [Swagger](http://15.164.170.252:8080/swagger-ui.html#!)
2. [Github Wiki](https://github.com/juhyeon96/Sns-timeline/wiki)
&nbsp;
### Demo

<div align="center">

1. timeline & user profile
&nbsp;
![image2](https://user-images.githubusercontent.com/45280737/72735109-80542c80-3bde-11ea-8abe-ab2014639915.png)
&nbsp;
&nbsp;

2. post create
&nbsp;
<img width="1680" alt="스크린샷 2020-01-20 오후 9 45 10" src="https://user-images.githubusercontent.com/45280737/72735037-5f8bd700-3bde-11ea-853a-b91d8305f679.png">
&nbsp;
&nbsp;

3. newsfeed
&nbsp;
<img width="1680" alt="스크린샷 2020-01-20 오후 9 46 26" src="https://user-images.githubusercontent.com/45280737/72735005-526ee800-3bde-11ea-9af0-386d4a9b4b8a.png">
&nbsp;
&nbsp;

4. alarm
&nbsp;
<img width="1680" alt="스크린샷 2020-01-20 오후 9 52 12" src="https://user-images.githubusercontent.com/45280737/72736098-6f0c1f80-3be0-11ea-9660-80a199b67a39.png">
&nbsp;
&nbsp;


</div>

<!-- CONTACT -->
&nbsp;

## Contact

 - 김주현 ([juhyeon96](https://github.com/juhyeon96)) 
 - 정다은 ([jde](https://github.com/jeongdaeun98))



