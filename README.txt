Title: Client Scheduling System
Author: Luis Estevez <lestev3@wgu.edu>
Version: 1.0
Completion date: March 19, 2022

Purpose: Client Scheduling System is for tracking and scheduling appointments for customers who we can create and delete
         from database. All this data is used to generate reports.

TECH_USED
IDE: IntelliJ Community 2021.3
JDK version: Java 17.0.1
JavaFX version: JavaFX-SDK-17.0.1
MySQL Connector Driver version: mysql-connector-java-8.0.25

DIRECTIONS: When opening the application, we're prompted to login in order to access the database of appointments,
            customers and generated reports.After login, you're directed to the main view where you can manage
            appointments at the top and customers at the bottom. In both sections you will three buttons
            add, modify and delete that allows us to add appointment or customer, modify existing appointment or
            customer, and delete appointment or customer, respectively. At the top of the main view, you will see a
            menubar that allows us to log out of Client Scheduling System, and generate Reports.

          When clicking generate reports, you will see three sections for three different reports. At the bottom, you're
          allowed to filter appointments by contact. At the top left, you can see the COUNT number of appointments by
          appointment type AND month. For the top right section, read DESCRIPTION OF ADDITIONAL REPORT.

DESCRIPTION OF ADDITIONAL REPORT: In the top right side of the report window, we can see a list of customers whose address
                                  is not in the United States. When selecting a customer, you will see a COUNT number of
                                  appointments from past, present and future.