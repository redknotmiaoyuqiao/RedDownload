#这是一个多线程下载工具

##示范

DownloadListener 是一个抽象类，是下载的监听，在使用的时候，你可以继承这个类，覆写里面的三个抽象方法，以达到监听下载的功能


- 需要用到的权限
<pre>
android.permission.INTERNET
android.permission.WRITE_EXTERNAL_STORAGE
</pre>

- 监听类
<pre>
private class MyDownloadListener extends DownloadListener {

        @Override
        public void onReady(File file) {
            //这个方法在获取到服务器文件详情后被调用，File表示的是在本地创建的文件。文件大小和服务器上相同。
        }

        @Override
        public void onUpdate(int process) {
            //这个方法会在文件下载进度改变的时候被调用，process表示文件下载进度，从0到100
        }

        @Override
        public void onComplete() {
            //这个方法会在文件下载完成之后被调用。
        }
}
</pre>


- DownloadSession 表示一次下载会话（下载一个文件）
<pre>
//构造方法中传入的是一个监听对象
DownloadSession session = new DownloadSession(myDownloadListener);
//调用start方法可以开始下载，url表示下载地址，savePath表示保存路径，threadNum表示要开启的线程
session.start(String url,String savePath,int threadNum);
</pre>