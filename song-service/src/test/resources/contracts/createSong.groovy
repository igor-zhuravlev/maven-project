package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "should create song metadata"
    label "create_song_metadata"

    request {
        method POST()
        url "/songs"
        headers {
            contentType(applicationJson())
        }
        body(
            id: 1,
            name: "Song",
            artist: "Artist",
            album: "Album",
            duration: "01:15",
            year: "2026"
        )
    }

    response {
        status OK()
        headers {
            contentType(applicationJson())
        }
        body(
            id: 1
        )
    }
}
