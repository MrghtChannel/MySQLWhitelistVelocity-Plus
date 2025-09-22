# MySQLWhitelistVelocity-Plus

Velocity plugin for MySQL/MariaDB whitelist.

## ⚙️ Config (`config.properties`)
```properties
enabled=true
host=127.0.0.1
port=3306
database=test
user=root
password=example
message=To join the server, first link your account via t.me/your_bot or your website
```

## 📋 SQL
```sql
CREATE TABLE IF NOT EXISTS Whitelist (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    MinecraftName VARCHAR(32) NOT NULL UNIQUE
);
INSERT INTO Whitelist (MinecraftName) VALUES ('PlayerName');
```

## 🚀 Installation
1. Put the `.jar` into the `plugins/` folder.
2. Start the server → config will be created.
3. Edit database settings.
4. Add players into the `Whitelist`.

---

# MySQLWhitelistVelocity-Plus

Плагін Velocity для whitelist через MySQL/MariaDB.

## ⚙️ Конфіг (`config.properties`)
```properties
enabled=true
host=127.0.0.1
port=3306
database=test
user=root
password=example
message=Для того, щоб зайти на сервер, спочатку підключіть свій акаунт до t.me/ваш_бот або ваш сайт
```

## 📋 SQL
```sql
CREATE TABLE IF NOT EXISTS Whitelist (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    MinecraftName VARCHAR(32) NOT NULL UNIQUE
);
INSERT INTO Whitelist (MinecraftName) VALUES ('PlayerName');
```

## 🚀 Встановлення
1. Перемістіть `.jar` у папку `plugins/`.
2. Запустіть сервер → зʼявиться config.
3. Відредагуйте налаштування БД.
4. Додайте гравців у `Whitelist`.

---

# MySQLWhitelistVelocity-Plus

Плагин Velocity для whitelist через MySQL/MariaDB.

## ⚙️ Конфиг (`config.properties`)
```properties
enabled=true
host=127.0.0.1
port=3306
database=test
user=root
password=example
message=Чтобы зайти на сервер, сначала подключите свой аккаунт через t.me/ваш_бот или ваш сайт
```

## 📋 SQL
```sql
CREATE TABLE IF NOT EXISTS Whitelist (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    MinecraftName VARCHAR(32) NOT NULL UNIQUE
);
INSERT INTO Whitelist (MinecraftName) VALUES ('PlayerName');
```

## 🚀 Установка
1. Поместите `.jar` в папку `plugins/`.
2. Запустите сервер → появится config.
3. Отредактируйте настройки БД.
4. Добавьте игроков в `Whitelist`.





This project is based on [MySQLWhitelistVelocity](https://github.com/moesnow/MySQLWhitelistVelocity) by moesnow.  
Additional fixes and improvements are provided in [MySQLWhitelistVelocity-Plus](https://github.com/MrghtChannel/MySQLWhitelistVelocity-Plus).

