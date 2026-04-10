package org.example.zaivki.controller

import org.example.zaivki.dto.TicketRequestDto
import org.example.zaivki.dto.TicketResponseDto
import org.example.zaivki.service.TicketService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/tickets")
class TicketController(private val ticketService: TicketService) {

    @PostMapping
    fun createTicket(@RequestBody dto: TicketRequestDto): ResponseEntity<TicketResponseDto> {
        val ticket = ticketService.createTicket(dto)
        return ResponseEntity.status(HttpStatus.CREATED).body(ticket)
    }

    @GetMapping
    fun getAllTickets(): ResponseEntity<List<TicketResponseDto>> {
        return ResponseEntity.ok(ticketService.getAllForReport())
    }

    @GetMapping("/{id}")
    fun getTicketById(@PathVariable id: Long): ResponseEntity<TicketResponseDto> {
        return ResponseEntity.ok(ticketService.getTicketById(id))
    }

    @PatchMapping("/{id}/assignment")
    fun takeToWork(
        @PathVariable id: Long,
        @RequestParam workerId: Long
    ): ResponseEntity<Void> {
        ticketService.takeToWork(id, workerId)
        return ResponseEntity.noContent().build()
    }

    @PatchMapping("/{id}/completion")
    fun completeTicket(
        @PathVariable id: Long,
        @RequestParam rating: Int,
        @RequestParam review: String
    ): ResponseEntity<Void> {
        ticketService.completeTicket(id, rating, review)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/{id}/incidents")
    fun reportInjury(
        @PathVariable id: Long,
        @RequestParam workerLastName: String
    ): ResponseEntity<Void> {
        ticketService.handleInjury(id, workerLastName)
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }
}