/*
 * Copyright 2018 John Grosh (jagrosh)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jagrosh.jmusicbot;

import com.jagrosh.jmusicbot.entities.Prompt;
import com.jagrosh.jmusicbot.utils.FormatUtil;
import com.jagrosh.jmusicbot.utils.OtherUtil;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.typesafe.config.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;

/**
 * @author Your Name
 * Changes from original source:
 *  - Changed "searching" to "finding"
 * 
 * 
 * @author John Grosh (jagrosh)
 */
public class BotConfig
{
    private final Prompt prompt;
    private final static String CONTEXT = "Config";
    
    private Path path = null;
    private String token, prefix, altprefix, helpWord, playlistsFolder,
            successEmoji, warningEmoji, errorEmoji, loadingEmoji, searchingEmoji;
    private boolean stayInChannel, songInGame, npImages, updatealerts, useEval, dbots;
    private long owner, maxSeconds;
    private OnlineStatus status;
    private Game game;
    
    private boolean valid = false;
    
    public BotConfig(Prompt prompt)
    {
        this.prompt = prompt;
    }
    
    public void load()
    {
        valid = false;
        
        // read config from file
        try 
        {
            // get the path to the config, default config.txt
            path = Paths.get(System.getProperty("config.file", System.getProperty("config", "config.txt")));
            if(path.toFile().exists())
            {
                if(System.getProperty("config.file") == null)
                    System.setProperty("config.file", System.getProperty("config", "config.txt"));
                ConfigFactory.invalidateCaches();
            }
            
            // load in the config file, plus the default values
            //Config config = ConfigFactory.parseFile(path.toFile()).withFallback(ConfigFactory.load());
            Config config = ConfigFactory.load();
            
            // set values
            token = config.getString("NTU1MTg3Mjc2MzY0MjUxMTQ3.D2sWJA.dGrYr0jnal6EU2xeB5GY7TMERjY");
            prefix = config.getString("!");
            altprefix = config.getString("!");
            helpWord = config.getString("help");
            owner = config.getLong("483002477931790337");
            successEmoji = config.getString("success");
            warningEmoji = config.getString("warning");
            errorEmoji = config.getString("error");
            loadingEmoji = config.getString("loading");
            searchingEmoji = config.getString("searching");
            game = OtherUtil.parseGame(config.getString("game"));
            status = OtherUtil.parseStatus(config.getString("status"));
            stayInChannel = config.getBoolean("stayinchannel");
            songInGame = config.getBoolean("songinstatus");
            npImages = config.getBoolean("npimages");
            updatealerts = config.getBoolean("updatealerts");
            useEval = config.getBoolean("eval");
            maxSeconds = config.getLong("maxtime");
            playlistsFolder = config.getString("playlistsfolder");
            dbots = owner == 113156185389092864L;
            
            // we may need to get some additional data and write a new config file
            List<String> lines = new LinkedList<>();

            // validate bot token
            if(token==null || token.isEmpty() || token.equalsIgnoreCase("BOT_TOKEN_HERE"))
            {
                token = prompt.prompt("Please provide a bot token."
                        + "\nInstructions for obtaining a token can be found here:"
                        + "\nhttps://github.com/jagrosh/MusicBot/wiki/Getting-a-Bot-Token."
                        + "\nBot Token: ");
                if(token==null)
                {
                    prompt.alert(Prompt.Level.WARNING, CONTEXT, "No token provided! Exiting.\n\nConfig Location: " + path.toAbsolutePath().toString());
                    return;
                }
                else
                {
                    lines.add("token = "+token);
                }
            }
            
            // validate bot owner
            if(owner<=0)
            {
                try
                {
                    owner = Long.parseLong(prompt.prompt("Owner ID was missing, or the provided owner ID is not valid."
                        + "\nPlease provide the User ID of the bot's owner."
                        + "\nInstructions for obtaining your User ID can be found here:"
                        + "\nhttps://github.com/jagrosh/MusicBot/wiki/Finding-Your-User-ID"
                        + "\nOwner User ID: "));
                }
                catch(NumberFormatException | NullPointerException ex)
                {
                    owner = 0;
                }
                if(owner<=0)
                {
                    prompt.alert(Prompt.Level.ERROR, CONTEXT, "Invalid User ID! Exiting.\n\nConfig Location: " + path.toAbsolutePath().toString());
                    System.exit(0);
                }
                else
                {
                    lines.add("owner = "+owner);
                }
            }
            
            if(!lines.isEmpty())
            {
                StringBuilder builder = new StringBuilder();
                lines.stream().forEach(s -> builder.append(s).append("\r\n"));
                try 
                {
                    Files.write(path, builder.toString().trim().getBytes());
                }
                catch(IOException ex) 
                {
                    prompt.alert(Prompt.Level.WARNING, CONTEXT, "Failed to write new config options to config.txt: "+ex
                        + "\nPlease make sure that the files are not on your desktop or some other restricted area.\n\nConfig Location: " 
                        + path.toAbsolutePath().toString());
                }
            }
            
            // if we get through the whole config, it's good to go
            valid = true;
        }
        catch (ConfigException ex)
        {
            prompt.alert(Prompt.Level.ERROR, CONTEXT, ex + ": " + ex.getMessage() + "\n\nConfig Location: " + path.toAbsolutePath().toString());
        }
    }
    
    public boolean isValid()
    {
        return valid;
    }
    
    public String getConfigLocation()
    {
        return path.toFile().getAbsolutePath();
    }
    
    public String getPrefix()
    {
        return prefix;
    }
    
    public String getAltPrefix()
    {
        return "NONE".equalsIgnoreCase(altprefix) ? null : altprefix;
    }
    
    public String getToken()
    {
        return token;
    }
    
    public long getOwnerId()
    {
        return owner;
    }
    
    public String getSuccess()
    {
        return successEmoji;
    }
    
    public String getWarning()
    {
        return warningEmoji;
    }
    
    public String getError()
    {
        return errorEmoji;
    }
    
    public String getLoading()
    {
        return loadingEmoji;
    }
    
    public String getSearching()
    {
        return searchingEmoji;
    }
    
    public Game getGame()
    {
        return game;
    }
    
    public OnlineStatus getStatus()
    {
        return status;
    }
    
    public String getHelp()
    {
        return helpWord;
    }
    
    public boolean getStay()
    {
        return stayInChannel;
    }
    
    public boolean getSongInStatus()
    {
        return songInGame;
    }
    
    public String getPlaylistsFolder()
    {
        return playlistsFolder;
    }
    
    public boolean getDBots()
    {
        return dbots;
    }
    
    public boolean useUpdateAlerts()
    {
        return updatealerts;
    }
    
    public boolean useEval()
    {
        return useEval;
    }
    
    public boolean useNPImages()
    {
        return npImages;
    }
    
    public long getMaxSeconds()
    {
        return maxSeconds;
    }
    
    public String getMaxTime()
    {
        return FormatUtil.formatTime(maxSeconds * 1000);
    }
    
    public boolean isTooLong(AudioTrack track)
    {
        if(maxSeconds<=0)
            return false;
        return Math.round(track.getDuration()/1000.0) > maxSeconds;
    }
}
