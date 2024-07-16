//
//  Server.swift
//  SimpleMessaging
//
//  Created by Nirson Niño Samson on 7/16/24.
//

// Server.swift
import Foundation
import Network

class Server {
    private var listener: NWListener?

    func start() {
        do {
            listener = try NWListener(using: .tcp, on: 12345)
            listener?.stateUpdateHandler = { newState in
                print("Server state: \(newState)")
            }

            listener?.newConnectionHandler = { newConnection in
                self.handleClient(connection: newConnection)
            }

            listener?.start(queue: .global())
        } catch {
            print("Failed to start server: \(error)")
        }
    }

    private func handleClient(connection: NWConnection) {
        connection.start(queue: .global())
        connection.receiveMessage { (data, context, isComplete, error) in
            if let data = data, let message = String(data: data, encoding: .utf8) {
                print("Received: \(message)")
                let response = "Echo: \(message)"
                connection.send(content: response.data(using: .utf8), completion: .contentProcessed({ _ in }))
            }
        }
    }

    func stop() {
        listener?.cancel()
    }
}
