# Database setup (run once per computer)

The app saves learned words to a local MySQL/MariaDB database named `sentence_builder`.
If the database is not reachable, the app still opens but works in memory only, and
the window title says "database offline".

## 1. Install and start the server (Mac, Homebrew)

```
brew install mysql
brew services start mysql
```

## 2. Create the tables

From the project folder:

```
mysql -u root < src/main/resources/database/schema.sql
```

If your root user has a password, use `mysql -u root -p < ...` instead.
Running it again is safe: it only creates what is missing.

## 3. Tell the app how to connect (only if you changed the defaults)

Defaults: `jdbc:mysql://localhost:3306/sentence_builder`, user `root`, empty password.
To override, set these in the same terminal before running:

```
export SB_DB_USER=root
export SB_DB_PASSWORD='your-password'
export SB_DB_URL='jdbc:mysql://localhost:3306/sentence_builder?rewriteBatchedStatements=true'
```

Never put a password in the code or commit it.

## 4. Run and check

```
./mvnw javafx:run
```

Import a text file, close the app, open it again, and check the Generate tab. "Known
words" should still show the count from before. To look at the data directly:

```
mysql -u root -e "SELECT COUNT(*) FROM sentence_builder.words;"
```

## Troubleshooting

- Title says "database offline": the server is not running, or the settings above are wrong.
  The reason is printed in the terminal ("Database unavailable ...").
- "No suitable driver found": add `requires com.mysql.cj;` to `module-info.java`.
- "Unknown database": step 2 was not run.
