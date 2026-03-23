from django.urls import path, include
from rest_framework.routers import DefaultRouter
from .views import (
    AuthorViewSet,
    GenreViewSet,
    BookViewSet,
    ReadingViewSet,
    ReadingSessionViewSet,
    AnnualGoalViewSet
)

router = DefaultRouter()

router.register(r'authors', AuthorViewSet)
router.register(r'genres', GenreViewSet)
router.register(r'books', BookViewSet)
router.register(r'readings', ReadingViewSet)
router.register(r'sessions', ReadingSessionViewSet)
router.register(r'goals', AnnualGoalViewSet)

urlpatterns = [
    path('', include(router.urls)),
]