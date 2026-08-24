module.exports = {
  apps: [
    {
      name: "student-service",
      script: "java.exe",
      args: "-jar student-service/target/Student-Service-1.0.0.jar",
      log_file: "./logs/student-service.log",
      env: { SPRING_PROFILES_ACTIVE: 'dev', SPRING_CLOUD_CONFIG_URI: 'http://localhost:9000' }
    },
    {
      name: "program-service",
      script: "java.exe",
      args: "-jar program-service/target/Program-Service-1.0.0.jar",
      log_file: "./logs/program-service.log",
      env: { SPRING_PROFILES_ACTIVE: 'dev', SPRING_CLOUD_CONFIG_URI: 'http://localhost:9000' }
    },
    {
      name: "enrollment-service",
      script: "java.exe",
      args: "-jar enrollment-service/target/Enrollment-Service-1.0.0.jar",
      log_file: "./logs/enrollment-service.log",
      env: { SPRING_PROFILES_ACTIVE: 'dev', SPRING_CLOUD_CONFIG_URI: 'http://localhost:9000' }
    }
  ]
}
