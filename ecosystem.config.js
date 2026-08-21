module.exports = {
  apps: [
    {
      name: "cloud-sql-auth-proxy",
      script: " ./cloud-sql-proxy hdse-capstone:asia-southeast1:mysql-vm hdse-capstone:asia-southeast1:postgres-vm --private-ip",
      log_file: "./logs/cloud-sql-proxy.log"

    },
    {
      name: "student-service",
      script: "java -jar student-service/target/Student-Service-1.0.0.jar",
      log_file: "./logs/student-service.log",
      instance: 2
    },


    {
      name: "program-service",
      script: "java -jar program-service/target/Program-Service-1.0.0.jar ",
      log_file: "./logs/program-service.log",
      instance: 2
    },
    {
      name: "enrollment-service",
      script: "java -jar enrollment-service/target/Enrollment-Service-1.0.0.jar ",
      log_file: "./logs/enrollment-service.log",
      instance: 2
    }
  ]
}
