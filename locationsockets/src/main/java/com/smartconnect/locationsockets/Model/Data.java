package com.smartconnect.locationsockets.Model;

public class Data
{
    private String password;

    private String userName;

    private String UDID;

    public String getPassword ()
    {
        return password;
    }

    public void setPassword (String password)
    {
        this.password = password;
    }

    public String getUserName ()
    {
        return userName;
    }

    public void setUserName (String userName)
    {
        this.userName = userName;
    }

    public String getUDID ()
    {
        return UDID;
    }

    public void setUDID (String UDID)
    {
        this.UDID = UDID;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [password = "+password+", userName = "+userName+", UDID = "+UDID+"]";
    }
}

