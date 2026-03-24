from django.urls import path, include
from rest_framework.routers import DefaultRouter
from .views import (
    AuthorViewSet,
    GenreViewSet,
    books,
    book_by_id,
    readings,
    reading_by_id,
    ReadingSessionViewSet,
    AnnualGoalViewSet
)



urlpatterns = [
    path('books/', books),
    path('books/<int:id>/', book_by_id),

    path('readings/', readings),
    path('readings/<int:id>/', reading_by_id),
]