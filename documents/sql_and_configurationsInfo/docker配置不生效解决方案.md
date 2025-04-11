# 解决方案

#### 配置

```
{
    "registry-mirrors": [
        "https://docker.m.daocloud.io",
        "https://noohub.ru",
        "https://huecker.io",
        "https://dockerhub.timeweb.cloud",
        "https://hub.rat.dev/",
        "https://docker.1panel.live/",
        "https://docker.nju.edu.cn",
        "https://dockerproxy.com"
    ],
    "dns" : [
        "114.114.114.114",
        "8.8.8.8"
    ]
}

```



#### 命令

```
sudo mkdir -p /etc/docker
sudo tee /etc/docker/daemon.json <<-'EOF'
{
    "registry-mirrors": [
        "https://cr.laoyou.ip-ddns.com",
        "https://docker.1panel.live",
        "https://image.cloudlayer.icu",
        "https://hub.fast360.xyz",
        "https://docker-0.unsee.tech",
        "https://docker.1panelproxy.com",
        "https://docker.tbedu.top",
        "https://dockerpull.cn",
        "https://docker.m.daocloud.io",
        "https://hub.rat.dev",
        "https://docker.kejilion.pro",
        "https://docker.hlmirror.com",
        "https://ccr.ccs.tencentyun.com",
        "https://docker.imgdb.de",
        "https://docker.melikeme.cn",
        "https://pull.loridocker.com",
        "https://docker.m.daocloud.io",
        "https://noohub.ru",
        "https://huecker.io",
        "https://dockerhub.timeweb.cloud",
        "https://hub.rat.dev/",
        "https://docker.1panel.live/",
        "https://docker.nju.edu.cn",
        "https://dockerproxy.com"
    ],
    "dns" : [
        "114.114.114.114",
        "8.8.8.8"
    ]
}
EOF
# 这里有个坑，需要先分别停掉 docker.service 和 docker.socket，然后再重启，否则可能配置不会生效(还是从官方镜像源拉取)
sudo systemctl stop docker.service
sudo systemctl stop docker.socket
sudo systemctl start docker.service
sudo systemctl start docker.socket

sudo systemctl daemon-reload
sudo systemctl restart docker

```

#### 操作

```
2.清理docker缓存(解决更换镜像源不生效)
docker system prune -a
3.重启docker服务
sudo service docker restart
4.重新拉取镜像
docker pull <image_name>
```

