# Java + Docker + Kubernetes Deployment Guide

## Project Name

**Java K8s Demo – Docker & Kubernetes Deployment**

This project demonstrates how to:

* Build a Java Spring Boot application
* Create a Docker image
* Run the application using Docker
* Push the Docker image to Docker Hub
* Deploy the application on Kubernetes
* Create a Kubernetes Service
* Access and test the application in a web browser
* Scale the application using Kubernetes

---

# 1. Prerequisites

Install the following tools on Windows:

* Java 17
* Maven
* Git
* Docker Desktop
* Kubernetes enabled in Docker Desktop
* kubectl
* GitHub account
* Docker Hub account

Check the installations:

```bash
java -version
```

```bash
mvn -version
```

```bash
docker --version
```

```bash
kubectl version --client
```

Check Kubernetes:

```bash
kubectl get nodes
```

Expected:

```text
NAME             STATUS   ROLES           AGE
docker-desktop   Ready    control-plane   ...
```

If the node shows `Ready`, Kubernetes is running successfully.

---

# 2. Project Structure

```text
javak8/
│
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── demo/
│       │           └── k8sdemo/
│       │               └── K8sDemoApplication.java
│       │
│       └── resources/
│           ├── static/
│           │   ├── index.html
│           │   ├── style.css
│           │   └── script.js
│           │
│           └── application.properties
│
├── pom.xml
├── Dockerfile
│
└── k8s/
    ├── deployment.yaml
    └── service.yaml
```

---

# 3. Build the Java Application

Open Git Bash inside the project directory:

```bash
cd ~/Desktop/javak8
```

Build the Spring Boot application:

```bash
mvn clean package
```

If successful, you should see:

```text
BUILD SUCCESS
```

A JAR file will be created inside:

```text
target/
```

Example:

```text
target/k8s-demo-1.0.0.jar
```

---

# 4. Test the Java Application Locally

Before creating the Docker image, test the Java application.

Run:

```bash
java -jar target/k8s-demo-1.0.0.jar
```

The application runs on:

```text
http://localhost:8080
```

Open the browser:

```text
http://localhost:8080
```

### Important

If port `8080` is already being used by Jenkins, don't use this method.

You can directly continue with Docker testing using another host port.

Stop the Java application with:

```text
CTRL + C
```

---

# 5. Create Dockerfile

Create a file named:

```text
Dockerfile
```

Add:

```dockerfile
FROM eclipse-temurin:17-jre

WORKDIR /app

COPY target/k8s-demo-1.0.0.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Dockerfile Explanation

```dockerfile
FROM eclipse-temurin:17-jre
```

Uses Java 17 runtime.

```dockerfile
WORKDIR /app
```

Creates `/app` as the working directory inside the container.

```dockerfile
COPY target/k8s-demo-1.0.0.jar app.jar
```

Copies the Java JAR into the Docker image.

```dockerfile
EXPOSE 8080
```

Documents that the application uses port 8080.

```dockerfile
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Starts the Spring Boot application.

---

# 6. Create Docker Image

Make sure you are inside:

```text
~/Desktop/javak8
```

Run:

```bash
docker build -t javak8:v1 .
```

Explanation:

```text
docker build
```

Creates a Docker image.

```text
-t javak8:v1
```

Image name = `javak8`

Tag = `v1`

```text
.
```

Use the current directory as the build context.

Check the image:

```bash
docker images
```

You should see:

```text
REPOSITORY   TAG   IMAGE ID       ...
javak8       v1    xxxxxxxxxxxx   ...
```

---

# 7. Run Docker Container

Because Jenkins is using host port `8080`, use host port `8081`.

Run:

```bash
docker run -d --name javak8-container -p 8081:8080 javak8:v1
```

Explanation:

```text
8081:8080
```

means:

```text
Windows Host Port : Container Port
       8081       :      8080
```

Check the container:

```bash
docker ps
```

Expected:

```text
CONTAINER ID   IMAGE       PORTS
xxxxxxxx       javak8:v1   0.0.0.0:8081->8080/tcp
```

---

# 8. Test Docker Application in Browser

Open:

```text
http://localhost:8081
```

You should see:

**DevOps Deployment Hub**

Test the API:

```text
http://localhost:8081/api/status
```

Expected:

```text
Application is running successfully!
```

Test:

```text
http://localhost:8081/api/message
```

Expected:

```text
Java + Docker + Kubernetes deployment is working!
```

---

# 9. Stop and Remove Docker Container

Stop:

```bash
docker stop javak8-container
```

Remove:

```bash
docker rm javak8-container
```

Check:

```bash
docker ps -a
```

---

# 10. Login to Docker Hub

Login:

```bash
docker login
```

Enter your Docker Hub username and password/token.

---

# 11. Tag Docker Image for Docker Hub

Replace:

```text
YOUR_USERNAME
```

with your Docker Hub username.

Example:

```bash
docker tag javak8:v1 YOUR_USERNAME/javak8:v1
```

Check:

```bash
docker images
```

You should see something similar to:

```text
javak8                         v1
YOUR_USERNAME/javak8          v1
```

---

# 12. Push Image to Docker Hub

Run:

```bash
docker push YOUR_USERNAME/javak8:v1
```

Wait until the push completes successfully.

You can now find the image in your Docker Hub repository.

---

# 13. Check Kubernetes

Make sure Docker Desktop Kubernetes is running.

Run:

```bash
kubectl get nodes
```

Expected:

```text
docker-desktop   Ready
```

Check all Kubernetes resources:

```bash
kubectl get all
```

---

# 14. Kubernetes Deployment

Create:

```text
k8s/deployment.yaml
```

Use:

```yaml
apiVersion: apps/v1
kind: Deployment

metadata:
  name: javak8

spec:
  replicas: 2

  selector:
    matchLabels:
      app: javak8

  template:
    metadata:
      labels:
        app: javak8

    spec:
      containers:
        - name: javak8
          image: YOUR_USERNAME/javak8:v1

          ports:
            - containerPort: 8080

          resources:
            requests:
              memory: "128Mi"
              cpu: "100m"

            limits:
              memory: "512Mi"
              cpu: "500m"
```

Replace:

```text
YOUR_USERNAME
```

with your Docker Hub username.

For example:

```yaml
image: myusername/javak8:v1
```

---

# 15. Create Kubernetes Deployment

Run:

```bash
kubectl apply -f k8s/deployment.yaml
```

Expected:

```text
deployment.apps/javak8 created
```

Check deployment:

```bash
kubectl get deployment
```

Expected:

```text
NAME      READY   UP-TO-DATE   AVAILABLE
javak8    2/2     2            2
```

---

# 16. Check Kubernetes Pods

Run:

```bash
kubectl get pods
```

Expected:

```text
NAME                      READY   STATUS
javak8-xxxxxxxxxx-xxxxx   1/1     Running
javak8-xxxxxxxxxx-xxxxx   1/1     Running
```

You should have **2 Pods** because:

```yaml
replicas: 2
```

---

# 17. Check Pod Logs

Run:

```bash
kubectl logs deployment/javak8
```

You should see Spring Boot startup information similar to:

```text
Tomcat initialized with port 8080
Tomcat started on port 8080
Started K8sDemoApplication
```

This confirms that the Java application is running inside Kubernetes.

---

# 18. Create Kubernetes Service

Create:

```text
k8s/service.yaml
```

Use:

```yaml
apiVersion: v1
kind: Service

metadata:
  name: java-k8s-service

spec:
  type: NodePort

  selector:
    app: javak8

  ports:
    - protocol: TCP
      port: 8080
      targetPort: 8080
      nodePort: 30080
```

### Port Explanation

```text
port: 8080
```

Kubernetes Service port.

```text
targetPort: 8080
```

Application port inside the Pod.

```text
nodePort: 30080
```

Port used to expose the Service externally.

---

# 19. Create Kubernetes Service

Run:

```bash
kubectl apply -f k8s/service.yaml
```

Expected:

```text
service/java-k8s-service created
```

Check:

```bash
kubectl get service
```

Expected:

```text
java-k8s-service   NodePort   ...   8080:30080/TCP
```

---

# 20. Check Kubernetes Endpoints

Run:

```bash
kubectl get endpoints java-k8s-service
```

You should see Pod IP addresses with port 8080.

Example:

```text
java-k8s-service
10.244.0.7:8080,10.244.0.8:8080
```

This confirms that the Service has found the application Pods.

---

# 21. Check Complete Kubernetes Setup

Run:

```bash
kubectl get all
```

You should see:

```text
Pods
Services
Deployments
ReplicaSets
```

Example:

```text
NAME                         READY   STATUS
pod/javak8-xxxxx             1/1     Running
pod/javak8-yyyyy             1/1     Running

NAME                       TYPE       PORT(S)
service/java-k8s-service   NodePort   8080:30080/TCP

NAME                     READY
deployment.apps/javak8   2/2
```

---

# 22. Test Kubernetes Application in Browser

Normally, the NodePort URL is:

```text
http://localhost:30080
```

However, with Docker Desktop Kubernetes on Windows, NodePort access may not always work as expected.

For reliable local testing, use Kubernetes port-forward.

Run:

```bash
kubectl port-forward service/java-k8s-service 8081:8080
```

Expected:

```text
Forwarding from 127.0.0.1:8081 -> 8080
Forwarding from [::1]:8081 -> 8080
```

**Keep this terminal running.**

---

# 23. Open Application in Browser

Open:

```text
http://localhost:8081
```

You should see:

```text
DevOps Deployment Hub
```

Test the API:

```text
http://localhost:8081/api/status
```

Expected:

```text
Application is running successfully!
```

Test:

```text
http://localhost:8081/api/message
```

Expected:

```text
Java + Docker + Kubernetes deployment is working!
```

---

# 24. Why We Use Port 8081 for Browser Testing

Jenkins is already using Windows host port `8080`.

Therefore:

```text
Jenkins
localhost:8080
```

We use:

```text
localhost:8081
```

for local port-forward testing.

The Java application itself still runs on:

```text
8080
```

inside the Docker container and Kubernetes Pod.

There is no conflict between:

```text
Jenkins host port 8080
```

and:

```text
Kubernetes Pod port 8080
```

---

# 25. Complete Architecture

```text
                 GitHub
                    │
                    │ Source Code
                    ▼
             Java Spring Boot
                    │
                    │ mvn clean package
                    ▼
              JAR Application
                    │
                    │ docker build
                    ▼
              Docker Image
              javak8:v1
                    │
                    │ docker push
                    ▼
                Docker Hub
                    │
                    │ image pull
                    ▼
        ┌──────────────────────┐
        │      Kubernetes      │
        │   Docker Desktop     │
        │                      │
        │  Deployment javak8   │
        │       │              │
        │   ┌───┴───┐          │
        │   ▼       ▼          │
        │ Pod 1   Pod 2        │
        │ :8080   :8080        │
        │   └───┬───┘          │
        │       ▼              │
        │ java-k8s-service     │
        │       │              │
        │    NodePort          │
        │      30080            │
        └───────┬──────────────┘
                │
                ▼
          Browser Testing
          localhost:8081
          via port-forward
```

---

# 26. Useful Kubernetes Commands

### Check Pods

```bash
kubectl get pods
```

### Check Deployments

```bash
kubectl get deployments
```

### Check Services

```bash
kubectl get services
```

### Check Everything

```bash
kubectl get all
```

### Check Pod Logs

```bash
kubectl logs <pod-name>
```

### Describe Pod

```bash
kubectl describe pod <pod-name>
```

### Describe Service

```bash
kubectl describe service java-k8s-service
```

### Scale Application

Increase from 2 to 5 Pods:

```bash
kubectl scale deployment javak8 --replicas=5
```

Check:

```bash
kubectl get pods
```

### Scale Back

```bash
kubectl scale deployment javak8 --replicas=2
```

---

# 27. Updating the Application

After changing the Java/HTML/CSS/JS code:

Build again:

```bash
mvn clean package
```

Build a new Docker image:

```bash
docker build -t javak8:v2 .
```

Tag it:

```bash
docker tag javak8:v2 YOUR_USERNAME/javak8:v2
```

Push:

```bash
docker push YOUR_USERNAME/javak8:v2
```

Update:

```text
k8s/deployment.yaml
```

Change:

```yaml
image: YOUR_USERNAME/javak8:v1
```

to:

```yaml
image: YOUR_USERNAME/javak8:v2
```

Apply:

```bash
kubectl apply -f k8s/deployment.yaml
```

Check rollout:

```bash
kubectl rollout status deployment/javak8
```

Check Pods:

```bash
kubectl get pods
```

---

# 28. Delete Kubernetes Resources

To remove the Deployment:

```bash
kubectl delete deployment javak8
```

To remove the Service:

```bash
kubectl delete service java-k8s-service
```

Or delete using YAML:

```bash
kubectl delete -f k8s/deployment.yaml
```

```bash
kubectl delete -f k8s/service.yaml
```

---

# 29. Troubleshooting

## Pods are not Running

Run:

```bash
kubectl get pods
```

Then:

```bash
kubectl describe pod <pod-name>
```

Check logs:

```bash
kubectl logs <pod-name>
```

---

## Service has no Endpoints

Run:

```bash
kubectl get endpoints java-k8s-service
```

If it shows:

```text
<none>
```

Check that the Service selector matches the Pod label.

Deployment:

```yaml
labels:
  app: javak8
```

Service:

```yaml
selector:
  app: javak8
```

They must match.

---

## localhost:30080 Does Not Work

Don't immediately rebuild the application.

First check:

```bash
kubectl get pods
```

```bash
kubectl get service
```

```bash
kubectl get endpoints java-k8s-service
```

Then use the reliable local test:

```bash
kubectl port-forward service/java-k8s-service 8081:8080
```

Open:

```text
http://localhost:8081
```

If this works, the application and Kubernetes Service are working correctly.

---

# 30. Final Verification Checklist

Before considering the project complete, verify:

```text
[✓] Java application builds successfully
[✓] Docker image created
[✓] Docker container runs
[✓] Application tested in browser
[✓] Docker image pushed to Docker Hub
[✓] Kubernetes node is Ready
[✓] Deployment created
[✓] 2 Pods are Running
[✓] Service created
[✓] Service has Pod endpoints
[✓] Kubernetes port-forward works
[✓] Application opens in browser
[✓] API endpoints work
```

---

# 31. Main Commands – Quick Reference

```bash
# Java build
mvn clean package

# Docker build
docker build -t javak8:v1 .

# Docker run
docker run -d --name javak8-container -p 8081:8080 javak8:v1

# Docker check
docker ps

# Docker login
docker login

# Docker tag
docker tag javak8:v1 YOUR_USERNAME/javak8:v1

# Docker push
docker push YOUR_USERNAME/javak8:v1

# Kubernetes check
kubectl get nodes

# Kubernetes deployment
kubectl apply -f k8s/deployment.yaml

# Kubernetes service
kubectl apply -f k8s/service.yaml

# Check pods
kubectl get pods

# Check deployment
kubectl get deployment

# Check service
kubectl get service

# Check endpoints
kubectl get endpoints java-k8s-service

# Check everything
kubectl get all

# Browser testing
kubectl port-forward service/java-k8s-service 8081:8080

# Browser
http://localhost:8081
```

---

# 32. Project Flow to Remember

```text
CODE
  ↓
MAVEN BUILD
  ↓
JAR FILE
  ↓
DOCKER IMAGE
  ↓
DOCKER CONTAINER
  ↓
DOCKER HUB
  ↓
KUBERNETES DEPLOYMENT
  ↓
PODS
  ↓
KUBERNETES SERVICE
  ↓
PORT-FORWARD
  ↓
BROWSER
```

**This is the complete basic Docker → Docker Hub → Kubernetes deployment workflow.**

