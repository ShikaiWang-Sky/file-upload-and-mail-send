package com.wang.servlet;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.ProgressListener;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

public class FileServlet extends javax.servlet.http.HttpServlet {
    protected void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {

        //判断上传的文件是普通表单还是带文件的表单
        if (!ServletFileUpload.isMultipartContent(request)) {
            return;     //终止方法运行,说明这是一个普通的表单,直接返回
        }

        //创建上传文件的保存路径,建议保存在WEB-INF下.安全,用户无法直接访问上传的文件
        String uploadPath = this.getServletContext().getRealPath("/WEB-INF/upload");
        File uploadFile = new File(uploadPath);
        if (!uploadFile.exists()) {
            uploadFile.mkdir();     //如果第一次上传,文件的路径不存在,则创建这个路径
        }

        //缓存,临时文件
        //临时路径,假如文件超过了预期的大小,我们就把它放在一个临时文件中,过几天自动删除,或者提醒用户转存为永久
        String tmpPath = this.getServletContext().getRealPath("/WEB-INF/tmp");
        File tmpFile = new File(tmpPath);
        if (!tmpFile.exists()) {
            tmpFile.mkdir();     //如果第一次上传,文件的路径不存在,则创建这个路径
        }

        //处理上传的文件,一般都需要通过流来获取,我们可以使用request.getInputStream(),原生态的文件上传流获取,十分麻烦
        //但是我们都建议使用Apache的文件上传组件来实现,common-fileload,它需要依赖于commons-io组件

        /*
        ServletFileUpload负责上传处理的文件,并将表单中每个输出项封装成一个FileItem对象
        在使用ServletFileUpload对象解析请求时需要DiskFileItemFactory对象.
        所以,我们需要在进行解析工作前构造好DiskFileItemFactory对象.
        通过ServletFileUpload对象的构造方法或setFileItemFactory()方法设置ServletFileUpload对象的fileItemFactory属性
         */

        //1.创建DiskFileItemFactory对象,处理文件上传路径或者大小限制
        DiskFileItemFactory factory = new DiskFileItemFactory();
        //通过这个工厂设置一个缓存区,当上传的文件大于这个缓存区的时候,将他放到临时文件中
        factory.setSizeThreshold(1024 * 1024);  //缓存区大小为1M
        factory.setRepository(tmpFile);         //临时目录的保存路径,需要一个File

        //2.获取ServletFileUpload
        ServletFileUpload upload = new ServletFileUpload(factory);
        //监听文件上传进度:
        upload.setProgressListener(new ProgressListener() {
            @Override
            //pBytesRead:已经读取到的文件大小
            //pContentLength:文件大小
            public void update(long pByteRead, long pContentLength, int PItem) {
                System.out.println("总大小: " + pContentLength + "已上传: " + pByteRead);
            }
        });

        //处理乱码问题
        upload.setHeaderEncoding("UTF-8");
        //设置单个文件的最大值
        upload.setFileSizeMax(1024 * 1024 * 10);
        //设置总共能够上传的文件的大小
        //1024kb * 1024kb = 1M
        upload.setSizeMax(1024 * 1024 * 10);

        //3.处理上传文件
        //把前端请求解析,封装成一个FileItem对象,需要从ServletFileUpload对象中获取
        try {
            List<FileItem> fileItems = upload.parseRequest(request);
            //fileItem  每一个表单对象
            for (FileItem fileItem : fileItems) {
                //判断上传的文件是普通的表单还是带文件的表单
                if (fileItem.isFormField()) {
                    //getFieldName指的是前端表单控件的name
                    String name = fileItem.getFieldName();
                    //处理乱码
                    String value = fileItem.getString("UTF-8");
                    System.out.println(name + " : " + value);
                } else {
                    //文件的情况下
                    //==================处理文件====================
                    String uploadFileName = fileItem.getName();
                    //可能存在文件名不合法的情况
                    if (uploadFileName.trim().equals("")) {
                        continue;
                    }
                    //获得上传的文件名  /images/pic.png
                    String fileName = uploadFileName.substring(uploadFileName.lastIndexOf("/") + 1);
                    //获得文件的后缀名
                    String fileExtName = uploadFileName.substring(uploadFileName.lastIndexOf(".") + 1);
                    /*
                        如果文件后缀名不是我们所需要的
                        就直接return,不处理,告诉用户文件类型不对
                     */

                    //可以使用UUID(唯一识别的通用码),保证文件名唯一
                    //UUID.randomUUID(),随机生成一个唯一识别的通用码
                    //网络传输中的东西,都需要序列化
                    //POJO,实体类,如果想要在多个电脑上运行,需要传输,需要把对象都序列化了
                    //JNI = Java Native Interface
                    //implements Serializable 序列化的标记接口  JVM ==> 本地方法栈   native ==> C++

                    String uuidPath = UUID.randomUUID().toString();

                    //==================存放地址====================

                    //存到哪里? uploadPath
                    //文件真实存在的路径 realPath
                    String realPath = uploadPath + "/" + uuidPath;
                    //给每一个文件创建一个对应的文件夹
                    File realPathFile = new File(realPath);
                    if (!realPathFile.exists()) {
                        realPathFile.mkdir();
                    }

                    //==================文件传输=====================
                    //获得文件上传的流
                    InputStream inputStream = fileItem.getInputStream();

                    //创建一个文件输出流
                    //realPath = 真实的文件夹
                    //文件:加上输出的文件的名字 + "/" + uuidFileName
                    FileOutputStream fos = new FileOutputStream(realPath + "/" + fileName);

                    //创建一个缓冲区
                    byte[] buffer = new byte[1024 * 1024];

                    //判断是否读取完毕
                    int len = 0;
                    //如果大于0说明还存在数据
                    while ((len = inputStream.read(buffer)) > 0) {
                        fos.write(buffer,0, len);
                    }

                    //关闭流
                    fos.close();
                    inputStream.close();

                    //上传成功,清除临时文件
                    fileItem.delete();
                }
            }

        } catch (FileUploadException e) {
            e.printStackTrace();
        }

        //Servlet请求转发消息
        request.getRequestDispatcher("info.jsp").forward(request, response);

    }

}
