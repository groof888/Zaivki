package org.example.zaivki.controller


import org.example.zaivki.dto.TicketRequestDto
import org.example.zaivki.entity.Ticket
import org.example.zaivki.service.TicketService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/tickets")
class TicketController(private val ticketService: TicketService) {

    @PostMapping("/create")
    fun createTicket(@RequestBody dto: TicketRequestDto) = ticketService.createTicket(dto)

    @GetMapping("/report")
    fun getAllTickets() = ticketService.getAllForReport()

    @PatchMapping("/{id}/take")
    fun takeToWork(@PathVariable id: Long, @RequestParam workerId: Long) {
        ticketService.takeToWork(id, workerId)
    }

    @PatchMapping("/{id}/complete")
    fun completeTicket(
        @PathVariable id: Long,
        @RequestParam rating: Int,
        @RequestParam review: String
    ) {
        ticketService.completeTicket(id, rating, review)
    }

    @PatchMapping("/{id}/injury")
    fun reportInjury(@PathVariable id: Long, @RequestParam workerLastName: String) {
        ticketService.handleInjury(id, workerLastName)
    }
}