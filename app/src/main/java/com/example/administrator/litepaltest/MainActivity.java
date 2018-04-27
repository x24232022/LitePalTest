package com.example.administrator.litepaltest;

import android.content.ContentValues;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.crud.DataSupport;
import org.litepal.crud.callback.SaveCallback;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button mBtn_add;
    private Button mBtn_delete;
    private Button mBtn_alter;
    private Button mBtn_selete;
    private TextView mTv_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBtn_add = findViewById(R.id.btn_add);
        mBtn_delete = findViewById(R.id.btn_delete);
        mBtn_alter = findViewById(R.id.btn_alter);
        mBtn_selete = findViewById(R.id.btn_select);
        mTv_text = findViewById(R.id.tv_text);

        mBtn_add.setOnClickListener(this);
        mBtn_delete.setOnClickListener(this);
        mBtn_alter.setOnClickListener(this);
        mBtn_selete.setOnClickListener(this);
    }

    public void add(){
        News news=new News();
        news.setTitle("这是第一条新闻的标题");
        news.setContent("第一条新闻的内容");
        news.setPublisDate(new Date());

        /**
         * 注意，所有存入数据库的表，这些类必须继承DataSupport这个类.
         * 只有继承了这个类才可以对表内的数据进行操作
         */
        //使用save()对单个实体数据进行保存,save()是不会抛出异常的，且返回的是boolean
        if(news.save()){
            Toast.makeText(this,"存储成功",Toast.LENGTH_SHORT).show();

        }else {
            Toast.makeText(this,"存储失败",Toast.LENGTH_SHORT).show();
        }
        //使用saveThrows()对单个实体数据进行保存,saveThrows()是当保存失败时会抛出异常的，所以，可以通过对这个异常进行捕获来处理存储失败的情况。
        news.saveThrows();
        //使用DataSupport.saveAll(）对多个实体数据进行保存
        List<News> mList=new ArrayList<>();
        DataSupport.saveAll(mList);

        //多对一的关系的数据保存,一条新闻多条评论
        Comment comment1 = new Comment();
        comment1.setContent("好评！");
        comment1.setPublishDate(new Date());
        comment1.save();
        Comment comment2 = new Comment();
        comment2.setContent("赞一个！");
        comment2.setPublishDate(new Date());
        comment2.save();
        News news1=new News();
        news1.getCommentList().add(comment1);
        news1.getCommentList().add(comment2);
        news1.setTitle("第二条新闻标题");
        news1.setContent("第二条新闻内容");
        news1.setPublisDate(new Date());
        news1.setCommentCount(news.getCommentList().size());
        news1.save();
        //使用saveAsync()对单个数据进行异步保存
        News news2=new News();
        news2.setTitle("这是一条新闻标题");
        news2.setContent("这是一条新闻内容");
        news2.setPublisDate(new Date());
        news2.saveAsync().listen(new SaveCallback() {
            @Override
            public void onFinish(boolean success) {

            }
        });

    }

    public void delete(){

        //删除news表中id为2的记录
        /**
         * 需要注意的是，这不仅仅会将news表中id为2的记录删除，
         * 同时还会将其它表中以news id为2的这条记录作为外键的数据一起删除掉（将和这条数据有关联的数据一起删掉），
         * 因为外键既然不存在了，那么这么数据也就没有保留的意义了。
         */
        DataSupport.delete(News.class, 2);
        //DataSupport中也提供了一个通过where语句来批量删除数据的方法
        //news表中标题为“今日iPhone6发布”且评论数等于0的所有新闻都删除掉
        DataSupport.deleteAll(News.class, "title = ? and commentcount = ?", "今日iPhone6发布", "0");
        //news表中所有的数据全部删除掉
        DataSupport.deleteAll(News.class);
        /**
         * 继承自DataSupport类的实例都可以通过调用delete()这个实例方法来删除数据。
         * 但前提是这个对象一定是要持久化之后的，就是调用了save()这一类增添的方法存储到库里的数据
         * 一个非持久化的对象如果调用了delete()方法则不会产生任何效果。
         */

        News news = new News();
        news.setTitle("这是一条新闻标题");
        news.setContent("这是一条新闻内容");
        news.save();

        news.delete();
        /**
         * 另外还有一个简单的办法可以帮助我们判断一个对象是否是持久化之后的，
         * DataSupport类中提供了一个isSaved()方法，
         * 这个方法返回true就表示该对象是经过持久化的，
         * 返回false则表示该对象未经过持久化。
         */
        if (news.isSaved()) {
            news.delete();
        }
    }

    public void updata(){
        /**
         * ContentValues类是SQL里封装的一个待修改数据的对象
         */
        //那么比如说我们想把news表中id为2的记录的标题改成“今日iPhone6发布”，就可以这样写：
        ContentValues values=new ContentValues();
        values.put("title","今日iphone6发布");
        DataSupport.update(News.class,values,2);
        //updateAll()方法表示修改多行记录，其中第一个参数仍然是Class，第二个参数还是ContentValues对象，第三个参数是一个conditions数组，用于指定修改哪些行的约束条件，返回值表示此次修改影响了多少行数据。
        //那么比如说我们想把news表中标题为“今日iPhone6发布”的所有新闻的标题改成“今日iPhone6 Plus发布”
        /**
         * 重点我们看一下最后的这个conditions数组，
         * 由于它的类型是一个String数组，
         * 我们可以在这里填入任意多个String参数，
         * 其中最前面一个String参数用于指定约束条件，
         * 后面所有的String参数用于填充约束条件中的占位符(即?号)，
         * 比如约束条件中有一个占位符，那么后面就应该填写一个参数，
         * 如果有两个占位符，后面就应该填写两个参数，以此类推。
         */
        ContentValues values1=new ContentValues();
        values1.put("title","今日iPhone6 Plus发布");
        DataSupport.updateAll(News.class,values1,"title=?","今日iPhone6发布");

        //比如说我们想把news表中标题为“今日iPhone6发布”且评论数量大于0的所有新闻的标题改成“今日iPhone6 Plus发布”

        ContentValues values2=new ContentValues();
        values2.put("title","今日iPhone6 Plus发布");
        DataSupport.updateAll(News.class, values2, "title = ? and commentcount > ?", "今日iPhone6发布", "0");

        //如果我们想把news表中所有新闻的标题都改成“今日iPhone6发布
        ContentValues values3 = new ContentValues();
        values3.put("title", "今日iPhone6 Plus发布");
        DataSupport.updateAll(News.class, values3);
        //LitePal提供了一种不需要ContentValues就能修改数据的方法
        //news表中id为2的记录的标题改成“今日iPhone6发布”
        News updateNews = new News();
        updateNews.setTitle("今日iPhone6发布");
        updateNews.update(2);
        //把news表中标题为“今日iPhone6发布”且评论数量大于0的所有新闻的标题改成“今日iPhone6 Plus发布”
        News updateNews1 = new News();
        updateNews1.setTitle("今日iPhone6发布");
        updateNews1.updateAll("title = ? and commentcount > ?", "今日iPhone6发布", "0");
        //将表中的其中一条数据修改成默认值
        //将commentCount的值设置成0
        News updateNews2 = new News();
        updateNews2.setToDefault("commentCount");
        updateNews2.updateAll();

    }
    public News selete(){
        //查询制定id的数据
        News news=DataSupport.find(News.class,1);
        //查询第一条数据
        News news1=DataSupport.findFirst(News.class);
        //查询最后一条数据
        News news2=DataSupport.findLast(News.class);
        //查询指定多个id的数据
        List<News> newsList=DataSupport.findAll(News.class,1,3,5,7,9);
        //查询表中所有的数据
        List<News> newsList1=DataSupport.findAll(News.class);

        //按照条件进行连缀查询
        //查询news表中所有评论数大于零的新闻
        List<News> newsList2= DataSupport.where("commentcount > ?", "0").find(News.class);
        //查询news表中所有评论数大于零的新闻，只要title和content这两列数据
        List<News> newsList3 = DataSupport.select("title", "content")
                .where("commentcount > ?", "0").find(News.class);
        //我希望将查询出的新闻按照发布的时间倒序排列，即最新发布的新闻放在最前面
        /**
         * order()方法中接收一个字符串参数，用于指定查询出的结果按照哪一列进行排序，
         * asc表示正序排序，desc表示倒序排序
         */
        List<News> newsList4 = DataSupport.select("title", "content")
                .where("commentcount > ?", "0")
                .order("publishdate desc").find(News.class);

        //只查询出前10条数据
        /**
         * limit()方法，这个方法接收一个整型参数，用于指定查询前几条数据，
         * 这里指定成10，意思就是查询所有匹配结果中的前10条数据
         */
        List<News> newsList5 = DataSupport.select("title", "content")
                .where("commentcount > ?", "0")
                .order("publishdate desc").limit(10).find(News.class);
        //展示第11到第20条新闻
        /**
         * offset()方法，用于指定查询结果的偏移量，这里指定成10，
         * 就表示偏移十个位置，那么原来是查询前10条新闻的，偏移了十个位置之后，
         * 就变成了查询第11到第20条新闻了，
         * 如果偏移量是20，那就表示查询第21到第30条新闻，以此类推
         */
        List<News> newsList6 = DataSupport.select("title", "content")
                .where("commentcount > ?", "0")
                .order("publishdate desc").limit(10).offset(10)
                .find(News.class);
        //激进查询
        //想要一次性将关联表中的数据也一起查询出来
        //我们想要查询news表中id为1的新闻，并且把这条新闻所对应的评论也一起查询出来，
        News news3 = DataSupport.find(News.class, 1, true);
        List<Comment> commentList = news3.getCommentList();
        return news;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_add:
                News news=selete();
                mTv_text.setText(news.toString());

                break;
            case R.id.btn_delete:
                break;
            case R.id.btn_alter:
                break;
            case R.id.btn_select:
                break;
            default:
                break;
        }
    }
}
