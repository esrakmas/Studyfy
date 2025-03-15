package com.example.studyfy.network

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.storage.Storage // Doğru import

val supabase = createSupabaseClient(
    supabaseUrl = "https://axldlwixhxkkmvietcis.supabase.co",
    supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImF4bGRsd2l4aHhra212aWV0Y2lzIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDIwNDMyMDQsImV4cCI6MjA1NzYxOTIwNH0.GRiPiMn1NXQvsXOEtUICYuGvwI0n-4edKLrdForL-r0"
) {
    install(Postgrest)  // Veritabanı işlemleri için
    install(Auth)       // Kimlik doğrulama için (eğer kullanıcı giriş sistemi yapacaksan)
    install(Realtime)   // Gerçek zamanlı veri desteği için (isteğe bağlı)
    install(Storage)    // Supabase Storage kullanacaksan (isteğe bağlı)
}




class SupBaseClient {
}