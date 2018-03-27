// By Pawel Mrozinski - S1425717
package mpdproject.gcu.me.org.TrafficIncidents;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ListAdapter;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    // Data Streams
    private String url1="http://trafficscotland.org/rss/feeds/currentincidents.aspx";
    private String url2="http://trafficscotland.org/rss/feeds/roadworks.aspx";
    private String url3="http://trafficscotland.org/rss/feeds/plannedroadworks.aspx";

    // Interface objects
    private Button startButton;
    private String result = "";
    private TextView pinned;

    // Items for dealing with lists
    ListView plannedIncidents;
    ListView listview;
    ListAdapter WidgetAdapter;

    // Selected incident descriptions for dialog providing details
    String incidentTitle;
    String incidentInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startButton = (Button)findViewById(R.id.startButton);
        listview = (ListView)findViewById(R.id.plannedIncidents);
        pinned = (TextView)findViewById(R.id.pinned);
        // Make pinned incident textview gone by default
        pinned.setVisibility(View.GONE);

        // On button click
        startButton.setOnClickListener(this);
        // On pinned click
        pinned.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Erase text
                pinned.setText(null);
                // Hide textview to remove empty space from interface
                pinned.setVisibility(View.GONE);
            }
        });
    } // End of onCreate

    public void onClick(View aview)
    {
        startProgress();
    }

    public void startProgress()
    {
        // Run network access on a separate thread;
        new Thread(new Task(url1)).start();
    }

    // Need separate thread to access the internet resource over network
    class Task implements Runnable
    {
    private String url;

        public Task(String aurl)
        {
            url = aurl;
        }
        @Override
        public void run()
        {
            URL aurl;
            URLConnection yc;
            BufferedReader in = null;
            String inputLine = "";

            // Logging activity concerning data access to logcat
            Log.e("MyTag","in run");

            try
            {
                Log.e("MyTag","in try");
                aurl = new URL(url1);
                yc = aurl.openConnection();
                in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
                Log.e("MyTag", "Success Connecting");

                // Throw away the first 2 header lines before parsing
                while ((inputLine = in.readLine()) != null)
                {
                    result = result + inputLine;
                    Log.e("MyTag",inputLine);
                }
                in.close();
            }
            catch (IOException ae)
            {
                Log.e("MyTag", "ioexception");
            }

            // Update the TextView to display raw XML
            MainActivity.this.runOnUiThread(new Runnable()
            {
                public void run() {
                    Log.d("UI thread", "I am the UI thread");
                    //urlInput.setText(result);
                    parseData(result);
                }
            });
        }
    }

    // Parsing Data with WidgetClass
    private LinkedList<String> parseData(String dataToParse)
    {
        final WidgetClass widget = new WidgetClass();
        final LinkedList<String> alist = new LinkedList<>();
        final LinkedList<String> desclist = new LinkedList<>();
        try
        {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput( new StringReader( dataToParse ) );
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT)
            {
                // Found a start tag
                if(eventType == XmlPullParser.START_TAG)
                {
                    // Check which Tag we have
                    if (xpp.getName().equalsIgnoreCase("channel"))
                    {

                    }
                    else
                    if (xpp.getName().equalsIgnoreCase("item"))
                    {
                        Log.e("MyTag","Item Start Tag found");
                    }
                    else
                    if (xpp.getName().equalsIgnoreCase("title"))
                    {
                        // Get the associated text
                        String temp = xpp.nextText();
                        // Describe text
                        Log.e("MyTag","Title is " + temp);
                        widget.settitle(temp);
                    }
                    else
                        // Check which Tag we have
                        if (xpp.getName().equalsIgnoreCase("description"))
                        {
                            // Get the associated text
                            String temp = xpp.nextText();
                            // Describe text
                            Log.e("MyTag","Description is " + temp);
                            widget.setdescription(temp);
                        }
                        else
                            // Check which Tag we have
                            if (xpp.getName().equalsIgnoreCase("pubDate"))
                            {
                                // Get the associated text
                                String temp = xpp.nextText();
                                // Describe text
                                Log.e("MyTag","Date is " + temp);
                                widget.setpubDate(temp);
                            }
                }
                else
                if(eventType == XmlPullParser.END_TAG)
                {
                    if (xpp.getName().equalsIgnoreCase("item"))
                    {
                        Log.e("MyTag","widget is " + widget.toString());
                        alist.add(widget.getTitle());
                        desclist.add(widget.getdescription());
                        //WidgetAdapter.notify();
                    }
                    else
                    if (xpp.getName().equalsIgnoreCase("channel"))
                    {
                        int size;
                        size = alist.size();
                        Log.e("MyTag","channel size is " + size);
                    }
                }
                // Get the next event
                eventType = xpp.next();
            } // End of while
        }

        catch (XmlPullParserException ae1)
        {
            Log.e("MyTag","Parsing error" + ae1.toString());
        }
        catch (IOException ae1)
        {
            Log.e("MyTag","IO error during parsing");
        }

        Log.e("MyTag","End document");

        // Send Planned Incident widgets to Adapter
        final ListAdapter WidgetAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, alist);
        ListView plannedIncidents = (ListView) findViewById(R.id.plannedIncidents);
        // Set incidents to list
        plannedIncidents.setAdapter(WidgetAdapter);

        // Click list item to show info dialog
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Pass clicked widget's position to dialog description
                incidentTitle = alist.get(position);
                incidentInfo = desclist.get(position);
                // Show incident info dialog
                showIncidentDialog();
            }
        });

        return alist;
        // End of List data parse
    }

    // More incident info dialog
    void showIncidentDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(incidentTitle);
        builder.setMessage(incidentInfo);
        builder.setCancelable(false);
        builder.setPositiveButton("Pin", confirmButtonListener);
        builder.setNegativeButton("Close", cancelButtonListener);
        AlertDialog alert = builder.create();
        alert.show();
    }

    // Add pin incidents
    private DialogInterface.OnClickListener confirmButtonListener = new DialogInterface.OnClickListener ()
    {
        public void onClick(DialogInterface dialog, int id)
        {
            // Make textview visible
            pinned.setVisibility(View.VISIBLE);
            // Print title and description to textview
            pinned.setText(incidentTitle + "\n\n" + incidentInfo);
        }
    };

    // Cancel and close dialog for more incident info
    private DialogInterface.OnClickListener cancelButtonListener = new DialogInterface.OnClickListener ()
    {
        public void onClick(DialogInterface dialog, int id)
        {
            dialog.cancel();
        }
    };
}
