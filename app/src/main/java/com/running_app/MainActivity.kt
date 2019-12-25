package com.running_app

// Import Dependencies
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

// Spotify Dependencies
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote

import com.spotify.protocol.client.Subscription
import com.spotify.protocol.types.PlayerState
import com.spotify.protocol.types.Track

// Spotify Credentials
private const val clientId: String = "7eb61dd3f23844b888b9f32ee25e8099"
private const val redirectUri: String = "com.running-app://callback"
private var spotifyAppRemote: SpotifyAppRemote? =  null


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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
            val playlistURI = "spotify:playlist:37i9dQZF1DX2sUQwD7tbmL"
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
