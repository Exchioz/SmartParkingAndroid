package com.example.bookingparkir.API

data class LoginRequest(
    val email: String,
    val password: String
)
data class LoginResponse(
    val message: String?,
    val id: Int
)
data class UserRequest(
    val id: String
)

data class UserResponse(
    val email: String,
    val nama: String,
    val telp: String,
    val platenomor: String,
    val saldo: String
)

data class UpdateUserRequest(
    val userid: String?,
    val email: String?,
    val telp: String?,
    val nama: String?,
    val platenomor: String?
)

data class TempatParkir(
    val id: Int,
    val namatempat: String,
    val harga: String,
    val alamat: String,
    val tersedia: Int?
)

data class TempatParkirDetail(
    val id: Int,
    val namatempat: String,
    val alamat: String,
    val longg: Double,
    val lat: Double,
    val harga: String,
    val kapasitas: Int,
    val tersedia: Int
)
data class Reservasi(
    val id: Int,
    val user_id: String?,
    val parkir_id: Int,
)

data class ReservasiResponse(
    val status: String
)

data class CheckResponse(
    val message: String
)

data class ChangePasswordRequest(
    val userid: String?,
    val oldPassword: String,
    val newPassword: String,
    val confirmPassword: String
)

data class SignUpRequest(
    val nama: String,
    val email: String,
    val telp: String,
    val password: String,
    val password2: String,
    val platenumber: String
)

data class FinishedReservationsResponse(
    val finished_reservations: List<FinishedReservation>
)

data class FinishedReservation(
    val reservasi_id: Int,
    val status: String,
    val nama_tempat_parkir: String,
    val alamat_tempat_parkir: String,
    val lama_waktu: String?,
    val total_biaya: String?,
)

data class OngoingReservationsResponse(
    val ongoing_reservations: List<OngoingReservation>
)

data class OngoingReservation(
    val reservasi_id: Int,
    val status: String,
    val nama_tempat_parkir: String,
    val alamat_tempat_parkir: String,
    val waktu_akhir: String?,
    val waktu_awal: String
)

data class PropertyModel(
    val id: Int,
    val name: String,
    val address: String,
    val price: String,
    val parking: Int
)

data class CancelReservasiResponse(
    val message: String
)

data class PaymentRequest(
    val userId: String?
)

data class PaymentResponse(
    val message: String
)

data class TopupResponse(
    val message: String
)

data class TopupRequest(
    val transactionId: String,
    val userId: String?,
    val jumlah: Int,
    val status: String
)

data class ExpiredTimeResponse(
    val waktu_akhir: String
)