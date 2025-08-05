# Java Shell Project

This project aims to build a custom, POSIX-compliant shell in Java. The goal is to create a fully functional command-line interpreter that supports core shell features like command execution, I/O redirection, and environment variable management, all built upon a clean, object-oriented architecture.

## Setup and Usage

### Prerequisites

*   [Java Development Kit (JDK)](https://www.oracle.com/java/technologies/downloads/) (Version 23 or higher)
*   [Apache Maven](https://maven.apache.org/download.cgi)

### Running the Shell

1.  **Clone the repository:**
    ```sh
    git clone <repository-url>
    cd <repository-directory>
    ```

2.  **Compile and Package:**
    Use Maven to build the project. This will create a self-contained executable JAR in the `target` directory.
    ```sh
    mvn clean package
    ```

3.  **Run the Shell:**
    Execute the JAR file to start an interactive session.
    ```sh
    java -jar target/codecrafters-shell.jar
    ```
    Alternatively, you can use the provided wrapper script:
    ```sh
    ./your_program.sh
    ```

## Features to Implement

The following features are planned for implementation:

*   **Environment Variable Handling:**
    *   An `export` command to set session-specific environment variables.
    *   Expansion of variables (e.g., `$VAR`) in command arguments.
*   **Globbing (`*`, `?`):** Expansion of wildcard patterns into a list of matching file names.
*   **Logical Operators (`&&`, `||`):** Conditional execution of commands.
*   **Background Execution (`&`):** Ability to run commands in the background.
