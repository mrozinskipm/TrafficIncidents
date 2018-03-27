// By Pawel Mrozinski - S1425717
package mpdproject.gcu.me.org.TrafficIncidents;

public class WidgetClass
{
    private String title;
    private String description;
    private String pubDate;

    public WidgetClass()
    {
        title = "";
        description = "";
        pubDate = "";
    }

    public WidgetClass(String atitle,String adescription,String apubDate)
    {
        title = atitle;
        description = adescription;
        pubDate = apubDate;
    }

    public String getTitle()
    {
        return title;
    }
    public void settitle(String atitle)
    {
        title = atitle;
    }

    public String getdescription()
    {
        return description;
    }
    public void setdescription(String adescription)
    {
        description = adescription;
    }

    public String getpubDate()
    {
        return pubDate;
    }
    public void setpubDate(String apubDate)
    {
        pubDate = apubDate;
    }

    public String toString()
    {
        String temp;

        temp = title + " " + description + " " + pubDate;

        return temp;
    }



} // End of class