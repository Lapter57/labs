#  Создание демонстрационной программы и тестовых сценариев по шифрованию данных стандартной библиотекой BouncyCastle

## Цель

Необходимо написать программу, которая использует BouncyCastle для шифрования и дешифрования данных. 
Также необходимо осуществить подпись данных сертификатом и последующую верификацию этих данных.

## Использование

Соберите проект:

```bash
$ gradle build
```

Выполните следующую команду, чтобы зашифровать данные:
```bash
$ java -jar build/libs/lab2-1.0-SNAPSHOT.jar -m "encr" -i "path to an input file" -o "path to an output file"
```

Выполните следующую команду, чтобы расшифровать данные:
```bash
$ java -jar build/libs/lab1-1.0-SNAPSHOT.jar -m "decr" -i "path to an input file" -o "path to an output file" \
  -a "alias of private key" -p "password of private key"
```

Выполните следующую команду, чтобы подписать данные:
```bash
$ java -jar build/libs/lab1-1.0-SNAPSHOT.jar -m "sign" -i "path to an input file" -o "path to an output file" \
  -a "alias of private key" -p "password of private key"
```

Выполните следующую команду, чтобы верифицировать данные:
```bash
$ java -jar build/libs/lab1-1.0-SNAPSHOT.jar -m "verif" -i "path to an input file"
```