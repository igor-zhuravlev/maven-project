package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "should publish resource uploaded event"
    label "resource_uploaded_event"

    input {
        triggeredBy("publishResourceUploaded()")
    }

    outputMessage {
        sentTo("resources.exchange")
        headers {
            messagingContentType(applicationJson())
        }
        body(
            resourceId: 1
        )
    }
}
