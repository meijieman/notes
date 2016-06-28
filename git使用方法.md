## git 提交代码到 osChina
---

### git 全局设置
    git config --global user.name "meijieman"
    git config --global user.email "1558667079@qq.com"
    
### 创建 git 仓库
    mkdir demo
    cd demo
    git init
    touch README.md
    git add README.md
    git commit - m "first commit"
    git remote add origin https://git.oschina.net/meijieman/demo.git
    git push -u origin master
    
### 对于已有项目
    cd exist_repo
    git remote add orign https://git.oschina.net/meijieman/exist.git
    git push -u orign master
   
