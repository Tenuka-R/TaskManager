# About 
A simple desktop task management app built using Java Swing for the UI and with PostgreSQL as the backend. 
Each task contains a title, description, priority and deadline

## Features:
+ Create, edit and delete tasks
+ Search for a task by the title
+ Sort tasks by title, description, priority or deadline
+ Compelte a task with ability to un-mark a task as compelte
+ Suggest the next best task to calculate based on creation data

## Tech stack
+ Java (Swing for the UI)
+ PostgreSQL database (using JDBC)
+ JCalendar (com.toedter.calendar.JDateChooser) to choose a date

## Requirements
  + JDK 17+
  + PostgresSQL installed and running locally
  + JCalendar library in classpath (jcalendar-1.4.jar)
  + PostgreSQL JDBC driver (postgresql-42.7.13.jar)

## Setup
Create a database named Tasks 
```sql
CREATE DATABASE Tasks
```
Create tasks table
```sql
CREATE TABLE tasks (
  id SERIAL PRIMARY KEY,
  title TEXT NOT NULL,
  description TEXT,
  priority VARCHAR(10) NOT NULL,
  deadline DATE NOT NULL,
  completed BOOLEAN NOT NULL DEFAULT FALSE
)
```


  
