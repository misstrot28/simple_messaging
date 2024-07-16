//
//  Client.swift
//  SimpleMessaging
//
//  Created by Nirson Niño Samson on 7/16/24.
//

import Foundation
import Network

class Client {
    private var connection: NWConnection?

    func connect(to serverIp: String) {
        connection = NWConnection(host: NWEndpoint.Host(serverIp), port: 12345, using: .tcp)
        connection?.start(queue: .global())

        connection?.stateUpdateHandler = { newState in
            print("Client state: \(newState)")
        }

        receiveMessages()
    }

    func sendMessage(_ message: String) {
        connection?.send(content: message.data(using: .utf8), completion: .contentProcessed({ _ in }))
    }

    private func receiveMessages() {
        connection?.receiveMessage { (data, context, isComplete, error) in
            if let data = data, let message = String(data: data, encoding: .utf8) {
                print("Received: \(message)")
            }
            self.receiveMessages()
        }
    }

    func disconnect() {
        connection?.cancel()
    }
}
