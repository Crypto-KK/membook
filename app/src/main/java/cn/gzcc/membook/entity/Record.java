package cn.gzcc.membook.entity;


public class Record {
    private Integer id;
    private String titleName;
    private String textBody;
    private Long longCreateTime;
    private Long longNoticeTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitleName() {
        return titleName;
    }

    public void setTitleName(String titleName) {
        this.titleName = titleName;
    }

    public String getTextBody() {
        return textBody;
    }

    public void setTextBody(String textBody) {
        this.textBody = textBody;
    }

    public Long getLongCreateTime() {
        return longCreateTime;
    }

    public void setLongCreateTime(Long longCreateTime) {
        this.longCreateTime = longCreateTime;
    }

    public Long getLongNoticeTime() {
        return longNoticeTime;
    }

    public void setLongNoticeTime(Long longNoticeTime) {
        this.longNoticeTime = longNoticeTime;
    }

    @Override
    public String toString() {
        return "Record{" +
                "id=" + id +
                ", titleName='" + titleName + '\'' +
                ", textBody='" + textBody + '\'' +
                ", longCreateTime=" + longCreateTime +
                ", longNoticeTime=" + longNoticeTime +
                '}';
    }
}
