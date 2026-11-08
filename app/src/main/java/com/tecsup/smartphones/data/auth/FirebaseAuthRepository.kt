package com.tecsup.smartphones.data.auth


import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tecsup.smartphones.domain.model.Rol
import com.tecsup.smartphones.domain.model.Sesion
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : AuthRepository {

    override suspend fun iniciarSesion(
        correo: String,
        clave: String
    ): Result<Sesion> {

        return try {
            val resultado = auth.signInWithEmailAndPassword(
                correo.trim(),
                clave
            ).await()

            val usuario = resultado.user
                ?: throw IllegalStateException(
                    "No se pudo obtener el usuario autenticado."
                )

            val sesion = leerSesion(
                uid = usuario.uid,
                correo = usuario.email ?: correo
            )

            Result.success(sesion)

        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun sesionActual(): Sesion? {

        val usuario = auth.currentUser ?: return null

        return leerSesion(
            uid = usuario.uid,
            correo = usuario.email ?: ""
        )
    }

    override fun cerrarSesion() {
        auth.signOut()
    }

    private suspend fun leerSesion(
        uid: String,
        correo: String
    ): Sesion {

        val documento = db
            .collection("usuarios")
            .document(uid)
            .get()
            .await()

        if (!documento.exists()) {
            throw IllegalStateException(
                "No existe un perfil para este usuario en Firestore."
            )
        }

        val rolTexto = documento.getString("rol")

        val rol = when (rolTexto) {
            "admin" -> Rol.ADMIN
            "usuario" -> Rol.USUARIO
            else -> throw IllegalStateException(
                "El rol del usuario no está configurado correctamente."
            )
        }

        return Sesion(
            uid = uid,
            correo = correo,
            rol = rol
        )
    }
}