module.exports = {
  apps: [
    {
      name: "student-service",
      script: "java",
      args: "-jar student-service/target/Student-Service-1.0.0.jar --spring.profiles.active=prod",
      log_file: "./logs/student-service.log"
    },
    {
      name: "program-service",
      script: "java",
      args: "-jar program-service/target/Program-Service-1.0.0.jar --spring.profiles.active=prod",
      log_file: "./logs/program-service.log"
    },
    {
      name: "enrollment-service",
      script: "java",
      args: "-jar enrollment-service/target/Enrollment-Service-1.0.0.jar --spring.profiles.active=prod",
      log_file: "./logs/enrollment-service.log"
    }
  ]
};
