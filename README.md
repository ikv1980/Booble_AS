## СОДЕРЖАНИЕ<a id="back">
1. [Настройка доступа по SSH](#1)  
1.1 [Предварительные настройки системы](#1_1)  
1.2 [Доступ по логину-паролю](#1_2)   
1.3 [Доступ по SSH-ключу](#1_3)  
2. [Установка и настройка GitLab-CE на VPS](#2)  
2.1 [Установка GitLab](#2_1)  
2.2 [Конфигурирование GitLab](#2_2)  
2.3 [Создание нового проекта](#2_3)  
2.4 [Установка SSH ключей для доступа к GitLab](#2_4)  
2.5 [Добавление runners для CI/CD GitLab](#2_5)  
2.5.1 [Runner через Docker](#2_5_1)  
2.5.2 [Runner на клиенте](#2_5_2)  
2.5.3 [Синтаксис файла CI/CD](#2_5_3)  
3. [Установка Docker (Docker Compose)](#3)  

100. [Дополнительные материалы](#100)  


## 1. Настройка доступа по SSH<a id="1"></a>[ ⬆️](#back)
>###  1.1 Предварительные настройки системы<a id="1_1"></a>[ ⬆️](#back)
После развертывания [сервера](https://console.cloud.ru/) на базе Ubuntu 22.04 заходим в него. Логин-пароль был получен во время установки.  
В консоли пишем команду `sudo su` -  для получения прав суперпользователя (root).  
Далее можно изменить при необходимости пароль командой `passwd`.  
Сразу делаем синхронизацию и обновление всех пакетов apt до последних версий командой `sudo apt update && sudo apt upgrade`  
Для доступа на сервер используем консоль в браузере (если предоставляет поставщик VPS) или настраиваем доступ по **SSH** (команда `nano /etc/ssh/sshd_config`:  

```php
# ------- доступ по логину-паролю
PermitRootLogin yes # разрешает администратору входить в систему через SSH
PasswordAuthentication yes  # разрешает аутентификацию по паролю

# ------- доступ по SSH-ключам
PermitRootLogin no  # Запретить вход под root
PasswordAuthentication no   # Отключить аутентификацию по паролю
PubkeyAuthentication yes    # Включить аутентификацию по публичному ключу
AuthorizedKeysFile .ssh/authorized_keys # Путь к файлу с авторизованными ключами. Тут можно добавить %h/.ssh/authorized_keys, где %h обозначает домашний каталог пользователя

# ------- поддержание SSH-сессии
TCPKeepAlive yes    # позволяет поддерживать соединение активным
ClientAliveInterval 300 # устанавливает интервал (в секундах) между сообщениями о состоянии
ClientAliveCountMax 60  # максимальное количество сообщений о состоянии
Port 2222 # новый порт 2222 для подключения по SSH. Главное не забыть его открыть в файрволе (группе безопасности)
```
после любых изменения файла **/etc/ssh/sshd_config** не забываем перезагружать сервис командой `sudo systemctl restart ssh`  

>### 1.2 Доступ по логину-паролю (не рекомендуется)<a id="1_2"></a>[ ⬆️](#back)
Тут мы получаем связку логин-пароль и по нему заходим по SSH на наш удаленный сервер по команде `ssh username@server_ip` (например `ssh user@111.222.333.444` или `ssh -p 2222 user@111.222.333.444`).  

Для операционных систем Windows можно использовать [Putty](https://www.putty.org/). На вкладке [Session] задаем IP-адрес сервера и порт 22(или другой) и жмем [Open]. Так же можно сохранить данные для входа, используя кнопку [Save].
>### 1.3 Доступ по SSH-ключу (предпочтительный)<a id="1_3"></a>[ ⬆️](#back) 

На сервере настраиваем доступ по SSH ключам и по паролю (по паролю потом отключим, при необходимости).
На клиенте (Ubuntu, Windows 11) вводим команду `ssh-keygen -t rsa -b 4096` для создания связки SSH ключей. Следуем инструкциям, чтобы сохранить ключи (по умолчанию они сохраняются в **~/.ssh/id_rsa** и **~/.ssh/id_rsa.pub)**.   

- **Ubuntu**: чтобы скопировать публичный ключ на сервер используем команду: `ssh-copy-id username@server_ip` (например `ssh-copy-id user@111.222.333.444`)
`);
- **Windows 11**: используем OpenSSH. Проверить наличие и при необходимости установить можно через *Windows-Система-Дополнительные компоненты-OpenSSH*. Отправить ключ на сервер `cat ~/.ssh/id_rsa.pub | ssh user@111.222.333.444 "mkdir -p ~/.ssh && cat >> ~/.ssh/authorized_keys"`;
- так же можно вручную скопировать публичный ключ в файл `nano .ssh/authorized_keys` (в домашнем каталоге пользователя).

Главное в обоих случаях иметь доступ на сервер по связке логин-пароль (для подтверждения изменений). После настройки доступа по ключу можно отключить способ авторизации *логин-пароль* в файле **/etc/ssh/sshd_config**. 

Для [cloud.ru](https://console.cloud.ru/) при создании ВМ можно указать сразу публичный ключ (`cat ~/.ssh/id_rsa.pub`) и далее заходить на ВМ через команду `ssh user@111.222.333.444`.  
Так же можно использовать [Putty](https://www.putty.org/). Запускаем утилиту *PuTTUgen*, жмем [Load] и считываем файл **cat ~/.ssh/id_rsa.**, далее [Save private key] и сохраняем с расширением *.ppk
Далее в PuTTY на вкладке [Session] задаем IP-адрес сервера и порт 22(или другой), так же заходим в Connection-SSH-Auth(включаем Allow attempted changes of username in SSH-2) и в Credentials указываем приватный ключ и жмем [Open]. Так же можно сохранить данные для входа, используя кнопку [Save].
Настройки доступа по **SSH** (команда `nano /etc/ssh/sshd_config`) не делаются. 

## 2. Установка и настройка GitLab-CE на VPS<a id="2"></a>[ ⬆️](#back)
Минимальные требования: 2 СPU 3000, 4 MB RAM, 20 GB SSD.  
> ### 2.1 Установка GitLab<a id="2_1"></a>[ ⬆️](#back)  

Перед установкой создадим доменное имя (например twkostik.ddns.net) для нашего IP посредством сайта [NoIP](https://www.noip.com/). Это необходимо для последующего получения SSL сертификата.  
Если не получим сертификат, то Git будет ругаться. Для отключения проверки сертификата на клиенте пишем команду `git config --global http.sslverify false`.

Заходим на официальный сайт [GitLab](https://about.gitlab.com/install/), выбираем Ubuntu и запускаем установку командами:  
`sudo apt update && sudo apt upgrade`  
`sudo apt-get install -y curl openssh-server ca-certificates tzdata perl`  
`curl https://packages.gitlab.com/install/repositories/gitlab/gitlab-ce/script.deb.sh | sudo bash`  
`sudo EXTERNAL_URL="https://twkostik.ddns.net" apt-get install gitlab-ce`  
> ### 2.2 Конфигурирование GitLab<a id="2_2"></a>[ ⬆️](#back)  

Настройки для Gitlab для оптимизации (`nano /etc/gitlab/gitlab.rb`, поиск по тексту Ctrl+W):
```php
puma['worker_timeout'] = 120
puma['worker_processes'] = 2
sidekiq['concurrency'] = 2
postgresql['shared_buffers'] = "256MB"
gitlab_rails['lfs_enabled'] = false
external_url 'https://twkostik.ddns.net'
```
После внесения изменений выполняем команды:
`sudo gitlab-ctl reconfigure`
`sudo gitlab-ctl restart`

После установки заходим в GitLab, меняем пароль root и создаем нового администратора для работы (*EXTERNAL_URL/admin/users/*). Первоначальный пароль **root** лежит тут `cat /etc/gitlab/initial_root_password`.
Теперь можно приступить к созданию проектов, настройки доступа по SSH и установки runner.  
> ### 2.3 Создание нового проекта<a id="2_3"></a>[ ⬆️](#back)  

Создание проекта (*Your work/Projects/New project/Create blank project*).  
После создания проекта выполняем команды для инициализации проекта и добавления его в GitLab. Можно как добавить пустой проект, так и добавить существующий.

> ### 2.4 Установка SSH ключей для доступа к GitLab<a id="2_4"></a>[ ⬆️](#back)  

SSH-ключи позволяют работать с GitLab, без регистрации пользователя. То есть он может полноценно работать с GitLab, но не иметь личного кабинета.  
На клиенте создаем SSH ключи и добавляем в GitLab ([пункт 1.2](#1_2))  

Добавление SSH-ключей:
-  **Admin area/Deploy keys**  
   Добавление *Deploy key* в GitLab. Ключ <u>не привязан</u> (Instance) к проекту. Его можно привязать к любому проекту через *User/Project/Repository Settings/Deploy keys*; 
- **User/Project/Repository Settings/Deploy keys**  
   Добавление *Deploy key* в проект на GitLab. Ключ <u>привязан</u> (Project) к проекту и применим только для текущего проекта.



> ### 2.5 Добавление runners для CI/CD GitLab<a id="2_5"></a>[ ⬆️](#back)  
GitLab Runner — это веб-приложение (агент), предназначенное для запуска и автоматического выполнения процессов CI/CD в GitLab. GitLab Runner выполняет задачи из файла .gitlab-ci.yml, который располагается в корневой директории вашего проекта.  
Добавление Runner:
- **Admin area/CI_CD/Runners**  
  Добавление *Runner* на GitLab. Runner <u>не привязан</u> к проекту (Instance). Его можно привязать к любому проекту через....
- **User/Project/Setting/CI_CD/Runners**  
  Добавление *Runner* в проект на GitLab. Runner <u>привязан</u> к проекту (Project) и применим только для текущего проекта.  \
  Соответственно в зависимости от нашего решения, как добавлять Runner, мы копируем registration token (Project или Instance).
> #### 2.5.1 Runner через Docker<a id="2_5_1"></a>[ ⬆️](#back)
На сервере GitLab расмотрим установку через [Docker](https://docs.gitlab.com/runner/install/docker.html). Будет установлен для проекта (Project).  
Команды:  

``` python
# --- Docker Install  
curl -fsSL https://get.docker.com -o get-docker.sh  
sh ./get-docker.sh  
# --- Runner Install
docker run -d --name gitlab-runner --restart always -v /srv/gitlab-runner/config:/etc/gitlab-runner -v /var/run/docker.sock:/var/run/docker.sock gitlab/gitlab-runner:latest
# --- Runner Register  
docker run --rm -it -v /srv/gitlab-runner/config:/etc/gitlab-runner gitlab/gitlab-runner register
# Настроечные параметры
# GitLab instance URL:  https://twkostik.ddns.net
# registration token:   копируем из Runners
# description:          server
# tags for the runner:  server
# enter an executor:    docker
# docker image:         ubuntu:20.04
```
> #### 2.5.2 Runner на клиенте<a id="2_5_2"></a>[ ⬆️](#back) 
На клиенте расмотрим установку [GitLab Runner](https://docs.gitlab.com/runner/install/linux-repository.html#installing-gitlab-runner). Для этого запустим команды:
``` python
# --- Runner Install
curl -L "https://packages.gitlab.com/install/repositories/runner/gitlab-runner/script.deb.sh" | sudo bash
apt-get install gitlab-runner
# --- Runner Register and Autostart
gitlab-runner register
systemctl enable gitlab-runner.service
# Настроечные параметры
# GitLab instance URL:  https://twkostik.ddns.net
# registration token:   копируем из Runners
# description:          client
# tags for the runner:  client
# enter an executor:    shell
systemctl enable gitlab-runner.service
```
После установки Runner на клиенте, ему необходимо предоставить администраторские права.

> #### 2.5.3 Синтаксис файла CI/CD<a id="2_5_3"></a>[ ⬆️](#back) 
Подробная информация по наполнению файла [.gitlab-ci.yml](https://docs.gitlab.com/ee/ci/yaml/) на официальном сайте GitLab.  
Пример файла **.gitlab-ci.yml**: 
``` yml
stages:
  - local                                     # этап local
  - client                                    # этап client

variables:                                    # переменные
  WORK_DIR: "/home/admin/client"

local_stage:                                  # запуск первой задачи
  stage: local    
  image: ubuntu:22.04
  tags:
    - local                                   # тег runner на сервере GitLab
  script:
    - echo "------------- LOCAL start stage ..."
    - mkdir build                             # создание папки в корне пользователя
    - cp server.sh build/                     # копирование файла в папку build/
    - cd build                                # переход в папку build/
    - echo '' >> server.sh                    # запись 'ничего' в файл
    # запись в файл команд
    - echo 'free -h' >> server.sh
    - echo 'cat /etc/os-release' >> server.sh
    - bash server.sh >> gitlab.log            # запуск скрипта и вывод результата в файл
    - cat gitlab.log                          # просмотр содержимого gitlab.log
    - echo "-------------LOCAL complete"
  artifacts:
    paths: 
    - build/                                  # сохранение артефактов
  #allow_failure: true                        # игнорировать ошибки в текущем stage

client_stage:                                 # запуск второй задачи
  stage: client
  tags:
    - client                                  # тег runner на клиенте
  script:
    - echo "------------- CLIENT start stage ..."
    - sudo rm -rf $WORK_DIR                   # удаление дирректории, созданной предыдушим pipeline
    - sudo mkdir $WORK_DIR                    # создание папки в корне пользователя
    - sudo touch $WORK_DIR/client.sh          # создание файла client.sh
    # запись в файл команд
    - sudo bash -c "echo '#!/bin/bash' > "$WORK_DIR"/client.sh"
    - sudo bash -c "echo 'free -h' >> "$WORK_DIR"/client.sh"
    - sudo bash -c "cd /home/admin/frame/ && git pull git@twkostik.ddns.net:ikv1980/demo.git"
    - sudo bash build/server.sh               # запуск скрипта из первого этапа    
    # - sudo bash -c "cd /home/admin/frame/ && git pull git@ikv1980.ddns.net:ikv1980/frame.git"
    - echo "-------------CLIENT complete"
  #when: manual                               # Запуск этапа вручную
```
Оба файла файла **.gitlab-ci.yml** и **server.sh** находятся в корне проекта. Можно сразу в файле **server.sh** прописать необходимые команды (например строчку `#!/bin/bash`), а можно так же команды добавлять в процессе выполнения stage (в примере показано).

Для gitlab-runner, который будет запускаться на клиенте необходимо предоставить администраторские права. На клиенте вводим команду `visudo` и прописывам строку в разделе *# User privilege specification*  
``` config
gitlab-runner ALL=(ALL) NOPASSWD: ALL
```
Для развертывания проекта на удаленном клиенте необходимо вначале сделать клонирование проекта командой (см.в проекте *Code-Clone with SSH*)  
`git clone git@twkostik.ddns.net:ikv1980/demo.git`  
Тут главное, чтобы был настроен доступ к GitLab по [SSH](#2_4), а затем уже в этапе *client_stage* файла **.gitlab-ci.yml** прописать команду  
`- sudo bash -c "cd /home/admin/demo/ && git pull git@twkostik.ddns.net:ikv1980/demo.git"`

## 3. Установка Docker (Docker Compose)<a id="3"></a>[ ⬆️](#back)
Минимальные требования: 2 СPU 3000, 4 MB RAM, 20 GB SSD.  








## 100. Дополнительные материалы<a id="100"></a>[ ⬆️](#back)
1. [Официальный сайт VirtualBox](https://www.virtualbox.org/wiki/Downloads);
2. [Официальный сайт Ubuntu](https://ubuntu.com/download);
3. [Установка Ubuntu на VirtualBox](https://www.youtube.com/watch?v=8GltRtcweNY);
4. [GitLab - установка и настройка](https://youtube.com/playlist?list=PLqVeG_R3qMSzYe_s3-q7TZeawXxTyltGC&si=rVa8zUiC8oxinWbx);  
5. [Docker Compose](https://docs.docker.com/compose/);



<table>

  <tr>
    <th>Наименование</th>
    <th>Параметры</th>
    <th>Описание</th>
    <th>Цена, руб</th>
  </tr>

  <tr style="vertical-align: top;">
    <td>
      <a href="https://ihor.online">ihor.online</a>
      <br><strong>VPN</strong>
      <br>ikv1980@gmail.com
    </td>
    <td>
      <a href="http://194.53.54.60:55555/">194.53.54.60:55555</a>
      <br><strong>Европа</strong>
      <br>1 CPU 2400
      <br>768 MB RAM
      <br>7 GB SSD
    </td>
    <td>
      <strong>VPN</strong>
      <br>(chatGpt, youtube)
    </td>
    <td>240,00/мес.</td>
  </tr>

  <!-- VPS - сервера -->
  <tr style="vertical-align: top;">
    <td>
      <a href="https://cloud.ru">cloud.ru</a>
      <br><strong>GITLAB</strong>
      <br>twkostik@mail.ru
      <br>79924288882
    </td>
    <td>
      <a href="http://176.109.111.80/">176.109.111.80</a>
      <br><strong>Россия</strong>
      <br>2 CPU 3000
      <br>4 MB RAM
      <br>30 GB SSD
    </td>
    <td>
      <strong>GitLab</strong>
    </td>
    <td>146,88/мес.</td>
  </tr>

  <tr style="vertical-align: top;">
    <td>
      <a href="https://cloud.ru">cloud.ru</a><br>
      <strong>DEV</strong>
      <br>ikv1980@mail.ru
      <br>79168153149
    </td>
    <td>
      <a href="http://213.171.25.72/">213.171.25.72</a><br>
      <strong>Россия</strong>
      <br>2 CPU 3000
      <br>4 MB RAM
      <br>30 GB SSD
    </td>
    <td>
      <strong>Development project</strong>
      <br>- mariaDB
      <br> <a href="http://213.171.25.72:3000">- phpMyAdmin</a>
      <br> <a href="http://213.171.25.72:8080">- Apache</a>
      <br> <a href="http://213.171.25.72:9090">- Nginx</a>
    </td>
    <td>146,88/мес.</td>
  </tr>

  <tr style="vertical-align: top;">
    <td>
      <a href="https://cloud.ru">cloud.ru</a><br>
      <strong>PROD</strong>
      <br>ikv1980@gmail.com
      <br>79855098044
    </td>
    <td>
      <a href="http://213.171.25.133/">213.171.25.133</a>
      <br>2 CPU 3000
      <br>4 MB RAM
      <br>30 GB SSD
    </td>
    <td>
      <strong>Production project</strong>
      <br>- mariaDB
      <br> <a href="http://213.171.25.133:3000">- phpMyAdmin</a>
      <br> <a href="http://213.171.25.133:8080">- Apache</a>
      <br> <a href="http://213.171.25.133:9090">- Nginx</a>
    </td>
    <td>146,88/мес.</td>
  </tr>

<!-- DNS имена -->
  <tr style="vertical-align: top;">
    <td>
      <a href="https://www.noip.com/login">noip.com</a>
      <br><strong>NoIP</strong>
      <br>ikv1980@gmail.com
    </td>
    <td>
      <a href="ikv1980.ddns.net">ikv1980.ddns.net</a>
      <br>(176.109.111.80)
    </td>
    <td>
      <strong>IP address to DNS</strong>
      <br>Получение доменного имени
    </td>
    <td>0,00/мес.</td>
  </tr>

  <tr style="vertical-align: top;">
    <td>
      <a href="https://www.noip.com/login">noip.com</a>
      <br><strong>NoIP</strong>
      <br>twkostik@gmail.com
    </td>
    <td>
      <a href="twkostik.ddns.net">twkostik.ddns.net</a>
      <br>(213.171.25.11)
    </td>
    <td>
      <strong>IP address to DNS</strong>
      <br>Получение доменного имени
    </td>
    <td>0,00/мес.</td>
  </tr>

</table>
