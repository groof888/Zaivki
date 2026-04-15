package org.example.zaivki.controller

import jakarta.validation.Valid
import org.example.zaivki.dto.CompletionRequest
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
    fun createTicket(@Valid @RequestBody dto: TicketRequestDto): ResponseEntity<TicketResponseDto> {
        return ResponseEntity.status(HttpStatus.CREATED).body(ticketService.createTicket(dto))
    }

    @GetMapping
    fun getAllTickets(): ResponseEntity<List<TicketResponseDto>> =
        ResponseEntity.ok(ticketService.getAllForReport())

    @GetMapping("/{id}")
    fun getTicketById(@PathVariable id: Long): ResponseEntity<TicketResponseDto> =
        ResponseEntity.ok(ticketService.getTicketById(id))

    @PatchMapping("/{id}/completion")
    fun completeTicket(
        @PathVariable id: Long,
        @Valid @RequestBody completion: CompletionRequest
    ): ResponseEntity<Void> {
        ticketService.completeTicket(id, completion.rating, completion.review)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/{id}/incidents")
    fun reportInjury(
        @PathVariable id: Long,
        @RequestParam masterId: Long
    ): ResponseEntity<Void> {
        ticketService.handleInjury(id, masterId)
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }
}