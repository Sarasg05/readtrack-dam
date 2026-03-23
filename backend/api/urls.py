from django.urls import path, include
from rest_framework.routers import DefaultRouter
from .views import (
    AuthorViewSet,
    GenreViewSet,
    BookViewSet,
    readings,
    reading_by_id,
    ReadingSessionViewSet,
    AnnualGoalViewSet
)



urlpatterns = [
    path('readings/', readings),
    path('readings/<int:id>/', reading_by_id),
]