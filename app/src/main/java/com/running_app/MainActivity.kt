package com.running_app

// Import Dependencies
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast

// Spotify Dependencies
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote

import com.spotify.protocol.client.Subscription
import com.spotify.protocol.types.PlayerState
import com.spotify.protocol.types.Track
import kotlinx.android.synthetic.main.activity_main.*

// Spotify Credentials
private const val clientId: String = "7eb61dd3f23844b888b9f32ee25e8099"
private const val redirectUri: String = "com.running-app://callback"
private var spotifyAppRemote: SpotifyAppRemote? =  null


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_toSteps.setOnClickListener(){
            Toast.makeText(this, "To the counting!", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, RunningActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStart(){
        super.onStart()
        val connectionParams = ConnectionParams.Builder(clientId)
            .setRedirectUri(redirectUri)
            .showAuthView(true)
            .build()
        SpotifyAppRemote.connect(this, connectionParams, object : Connector.ConnectionListener{
            override fun onConnected(appRemote: SpotifyAppRemote){
                spotifyAppRemote = appRemote
                Log.d("MainActivity", "Connected! Yay!")

                connected()
            }
            override fun onFailure(throwable: Throwable){
                Log.e("MainActivity", throwable.message, throwable)
            }
        })

    }

    private fun connected(){
        spotifyAppRemote?.let {
            // Play a playlist
            val playlistURI = "spotify:playlist:00ziqTk7b6edwqszRKeRbL"
            it.playerApi.play(playlistURI)
            // Subscribe to PlayerState
            it.playerApi.subscribeToPlayerState().setEventCallback {
                val track: Track = it.track
                Log.d("MainActivity", track.name + " by " + track.artist.name)
            }
        }
    }

    override fun onStop(){
        super.onStop()
        spotifyAppRemote?.let {
            SpotifyAppRemote.disconnect(it)
        }
    }
}
